package fakeunittest.actions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.index.IndexFilter;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ltk.core.refactoring.Change;

public class ClassFake_h {

	private List<ICPPASTFunctionDefinition> fakeClassFunction;
	private ASTVisitorImpl astVisitorImpl;
	private IBinding fakeClass;
	private String def;
	private String fakeSource;
	private String projectStr;
	private PrintStream print;
	
	/**
	 * The constructor
	 * @throws CoreException 
	 */
	public ClassFake_h( ASTVisitorImpl astVisitorImplIn, PrintStream printIn, String projectIn, String fakeSourceIn ) throws CoreException {
		astVisitorImpl = astVisitorImplIn;
		fakeClassFunction = astVisitorImpl.getFakeClass();
		ICPPASTFunctionDefinition functionDefinition = ( ICPPASTFunctionDefinition ) fakeClassFunction.get( 0 );
		IBinding binding = functionDefinition.getDeclarator().getName().resolveBinding();	
		fakeClass = binding.getOwner();
		print = printIn;
		projectStr = projectIn;
		fakeSource = fakeSourceIn;
	}

	void generateClassFake() {
		print.println( "generateClassFake" );
	}

	void printFunction( IASTTranslationUnit tu )
			throws CoreException, IOException, DOMException, InterruptedException {	
		print.println( fakeClass.getName() );

		IPath path_cpp = Path.fromOSString( "" + fakeSource.replaceAll( "src", "unittest" ).replaceAll( fakeClass.getName() + ".cpp", "Fake" + fakeClass.getName() + "-debug.h" ) );
		print.println( path_cpp.toOSString() );
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject( projectStr );
		IFile file_cpp = project.getFile( path_cpp );
		
		InputStream file_stream = new ByteArrayInputStream( ( printIncludes( tu )
				+ "\nvoid printFakeFunction();\n"
				+ printCons()
				+ printFakeMethod()
				+ printFakeFunction() ).getBytes() );
		
		if ( file_cpp.exists() ) {
			file_cpp.setContents( file_stream, true, true, null );
		} else {
			file_cpp.create( file_stream , true, null );
		}
		file_stream.close();
		
/*		ICProject cppProject = CoreModel.getDefault().getCModel().getCProject( projectStr );
		ICElement iceElement_cpp = cppProject.findElement( path_cpp );				
		ITranslationUnit translationUnit_h = ( ITranslationUnit ) iceElement_cpp.getAdapter( ITranslationUnit.class );
		IIndex index = CCorePlugin.getIndexManager().getIndex( cppProject );
		
		IASTTranslationUnit tu_h;
		index.acquireReadLock();
		try {
			tu_h = translationUnit_h.getAST( index, ITranslationUnit.AST_SKIP_ALL_HEADERS | ITranslationUnit.AST_SKIP_FUNCTION_BODIES );
			if ( tu_h != null ) {
				modifyAST( tu_h );
			}
		} finally {
			index.releaseReadLock();
			tu_h = null;
		}*/
	}

	private String printIncludes( IASTTranslationUnit tu ) throws DOMException, CoreException, InterruptedException {		
		Set<String> includes = new HashSet<String>();
		String includeFile;		
		String includeStr = "";
		
		for ( IASTPreprocessorStatement preProcStmt : tu.getAllPreprocessorStatements() ) {
			def = preProcStmt.getRawSignature();
			if ( def.contains( "ifndef" ) ) {
				def = def.replace( "#ifndef", "" ).trim();				
				break;
			}
		}
	
		includeStr += "#ifndef _FAKE_" + def
				+ "\n#define _FAKE_" + def + "\n\n";		

		for( ICPPASTFunctionDefinition i : fakeClassFunction ) {	
			ICPPASTFunctionDeclarator functionDeclarator = ( ICPPASTFunctionDeclarator ) i.getDeclarator();
			IBinding binding = i.getDeclarator().getName().resolveBinding();	
			ICPPFunction function = ( ICPPFunction ) binding;
			
			String functionName = function.getName();
			if ( function instanceof ICPPConstructor || functionName.contains( "~" ) ) {
				continue;
			}

			for ( ICPPASTParameterDeclaration memberParameter : functionDeclarator.getParameters() ) {
//				print.println( memberParameter.getDeclSpecifier().getClass().getName() + " " + memberParameter.getDeclSpecifier() );

				includeFile = astVisitorImpl.findIncludes( memberParameter.getDeclSpecifier() );
				if ( !includeFile.isEmpty() ) {
					print.println( includeFile );
					includes.add( includeFile );
				}
			}
//			print.println( i.getDeclSpecifier().getClass().getName() + " " + i.getDeclSpecifier() );
			includeFile = astVisitorImpl.findIncludes( i.getDeclSpecifier() );
			if ( !includeFile.isEmpty() ) {
				print.println( includeFile );
				includes.add( includeFile );
			}
		}
		
		includeStr += "#include \"" + fakeSource.replaceAll( "src", "unittest" ).replaceAll( ".cpp", ".h\"" ) + "\n\n";
		for( String i : includes ) {	
			includeStr += "#include \"" + i + "\"\n";
		}

		return includeStr;
	}

	private void modifyAST( IASTTranslationUnit tu ) throws InterruptedException, DOMException, CoreException {
		for ( IASTDeclaration declaration : tu.getDeclarations() ) {
			ASTRewrite r = ASTRewrite.create( declaration.getParent().getTranslationUnit() );
			print.println( "modifyAST " + declaration.getParent().getClass().getName() +  declaration.getParent().getRawSignature() );
			if ( declaration.getRawSignature().contentEquals( "void printFakeFunction();" ) ) {
				IASTNode lit = r.createLiteralNode( printCons()
						+ printFakeMethod()
						+ printFakeFunction() );
				r.replace( declaration, lit, null );
				Change c = r.rewriteAST();
				c.perform( new NullProgressMonitor() );
			}
		}
	}
	
	private String printCons() throws DOMException	{
		return "\n#include \"Fake.h\"\n" 
				+ "#include \"FakeMethodVerifiers.h\"\n\n"
				+ "namespace " + fakeClass.getScope() + " {\n\n" 
				+ "class Fake" + fakeClass.getName() + " : public Fake, public " + fakeClass.getName() + "\n{\npublic:\n" 
				+ "  explicit Fake" + fakeClass.getName() + "();\n"
				+ "  virtual ~Fake" + fakeClass.getName() + "();\n\n";
	}

	private String printFakeMethod() {
		return "  virtual void registerMethodWatcher( TestUtility::FakeMethodWatcher& methodWatcher );\n"
				+ "  virtual void verifyFakeMethodWasNotCalled( const std::string& testCondition );\n"
				+ "  virtual void verifyFakeMethodUsage( const std::string& testCondition );\n"
				+ "  virtual void resetFakeMethodUsage();\n\n";
	}

	private String printFakeFunction() throws DOMException, CoreException {
		String functionDecl = "";
		String fakeFunctionDecl = "";
		
		for( ICPPASTFunctionDefinition i : fakeClassFunction ) {	
			ICPPASTFunctionDeclarator functionDeclarator = ( ICPPASTFunctionDeclarator ) i.getDeclarator();
			IBinding binding = i.getDeclarator().getName().resolveBinding();	
			ICPPFunction function = ( ICPPFunction ) binding;
			
			String functionName = function.getName();
			if ( function instanceof ICPPConstructor || functionName.contains( "~" ) ) {
				continue;
			}

			ArrayList functionParameter = new ArrayList<String>();
			ArrayList functionParameterDeclSpecifier = new ArrayList<String>();
			
/*			for ( ICPPASTConstructorChainInitializer memberInitializer : functionDefinition.getMemberInitializers() ) {
				IASTName memberName = memberInitializer.getMemberInitializerId();
				print.println( memberName.toString() );
			}	
*/
			for ( ICPPASTParameterDeclaration memberParameter : functionDeclarator.getParameters() ) {
//				IASTName memberName = memberInitializer.get;
				functionParameter.add( memberParameter.getDeclarator().getName().toString() );
				functionParameterDeclSpecifier.add( memberParameter.getDeclSpecifier().toString() );
//				print.println( memberParameter.getRawSignature() + " " + memberParameter.getDeclSpecifier().getRawSignature() + " " + memberParameter.getDeclarator().getRawSignature() );
			}
			functionDecl += "  virtual " + i.getDeclSpecifier() + " " + i.getDeclarator().getRawSignature().replaceFirst( fakeClass.getName() + "::", "" ) + ";\n";
			fakeFunctionDecl += "  mutable TestUtility::FakeMethod< " + i.getDeclSpecifier() + "( " + String.join( ", ", functionParameterDeclSpecifier ) + " ) > fake_" + function.getName() + ";\n";
			
		}
		return functionDecl + "\n"
				+ fakeFunctionDecl 
				+ "\n};\n\n} // namespace " + fakeClass.getScope() + "\n\n#endif // _FAKE_" + def + "\n";
	}
}
