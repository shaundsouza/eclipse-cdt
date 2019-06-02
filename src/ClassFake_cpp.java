package fakeunittest.actions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTBreakStatement;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
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
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class ClassFake_cpp {

	private List<ICPPASTFunctionDefinition> fakeClassFunction;
	private ASTVisitorImpl astVisitorImpl;
	private List<ICPPASTFunctionDeclarator> fakeFunctionDeclarator;
	private List<String> constructorFakeParameterDeclSpecifier;
	private List<String> constructorFakeParameter;
	private IBinding fakeClass;
	private String def;
	private String fakeSource;
	private String projectStr;
	private PrintStream print;

	/**
	 * The constructor
	 * @throws CoreException 
	 */
	public ClassFake_cpp( ASTVisitorImpl astVisitorImplIn, PrintStream printIn, String projectIn, String fakeSourceIn )
			throws CoreException {
		astVisitorImpl = astVisitorImplIn;
		fakeClassFunction = astVisitorImpl.getFakeClass();		
		ICPPASTFunctionDefinition functionDefinition = ( ICPPASTFunctionDefinition ) fakeClassFunction.get( 0 );
		IBinding binding = functionDefinition.getDeclarator().getName().resolveBinding();	
		fakeClass = binding.getOwner();		
		fakeFunctionDeclarator = new ArrayList<ICPPASTFunctionDeclarator>();
		constructorFakeParameterDeclSpecifier = new ArrayList<String>();
		constructorFakeParameter = new ArrayList<String>();
		print = printIn;
		projectStr = projectIn;
		fakeSource = fakeSourceIn;
	}

	void generateClassFake() {
		print.println( "generateClassFake" );
	}

	void printFunction() throws CoreException, IOException, DOMException, InterruptedException {	
		print.println( fakeClass.getName() );

		IPath path_cpp = Path.fromOSString( "" + fakeSource.replaceAll( "src", "unittest" ).replaceAll( fakeClass.getName(), "Fake" + fakeClass.getName() + "-debug" ) );
		print.println( "printFunction " + path_cpp.toOSString() );
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject( projectStr );
		IFile file_cpp = project.getFile( path_cpp );
		
		InputStream file_stream = new ByteArrayInputStream( ( printIncludes()
				+ "\nvoid printFakeFunction();\n"
				+ printCons()
				+ printMethodWatcher()
				+ printMethodNotCalled()
				+ printMethodUsage()
				+ printResetMethodUsage()
				+ printFakeFunction() ).getBytes() );
		
		if ( file_cpp.exists() ) {
			file_cpp.setContents( file_stream, true, true, null );
		} else {
			file_cpp.create( file_stream , true, null );
		}
		file_stream.close();
		
/*		ICProject cppProject = CoreModel.getDefault().getCModel().getCProject( projectStr );
		ICElement iceElement_cpp = cppProject.findElement( path_cpp );				
		ITranslationUnit translationUnit_cpp = ( ITranslationUnit ) iceElement_cpp.getAdapter( ITranslationUnit.class );		
		IIndex index = CCorePlugin.getIndexManager().getIndex( cppProject );
		
		IASTTranslationUnit tu_cpp;
		index.acquireReadLock();
		try {
			tu_cpp = translationUnit_cpp.getAST( index, ITranslationUnit.AST_SKIP_ALL_HEADERS | ITranslationUnit.AST_SKIP_FUNCTION_BODIES );
			if ( tu_cpp != null ) {
				modifyAST( tu_cpp );
			}
		} finally {
			index.releaseReadLock();
			tu_cpp = null;
		}*/
	}
/*	
	private void printFakeTree( IASTNode node ) throws InterruptedException {
		print.println( node.getClass().getName() + node.getRawSignature() );

		for ( IASTNode child : node.getChildren() )			
			printFakeTree( child );
	}
*/
	private void modifyAST( IASTTranslationUnit tu ) throws InterruptedException, DOMException, CoreException {
		for ( IASTDeclaration declaration : tu.getDeclarations() ) {
			ASTRewrite r = ASTRewrite.create( declaration.getParent().getTranslationUnit() );
			print.println( "modifyAST " + declaration.getParent().getClass().getName() +  declaration.getParent().getRawSignature() );
			if ( declaration.getRawSignature().contentEquals( "void printFakeFunction();" ) ) {
				IASTNode lit = r.createLiteralNode( printCons()
						+ printMethodWatcher()
						+ printMethodNotCalled()
						+ printMethodUsage()
						+ printResetMethodUsage()
						+ printFakeFunction() );
				r.replace( declaration, lit, null );
				Change c = r.rewriteAST();
				c.perform( new NullProgressMonitor() );
			}
		}
	}
	
	private String printIncludes() throws CoreException, IOException { 
		String includeStr = "#include \"" + fakeSource.replaceAll( "src", "unittest" ).replaceAll( fakeClass.getName() + ".cpp", "Fake" + fakeClass.getName() + ".h\"" ) + "\n";			
		print.println( fakeSource );
		return includeStr;
	}
	
	private String printCons() throws DOMException, CoreException, InterruptedException	{
		String cons = "";
		String includeStr = "";
		List<String> includes = new ArrayList<String>();
		List<String> fakeFunctionStr = new ArrayList<String>();		
		List<String> consFakeStr = new ArrayList<String>();
		String fakeClassNamespaceStr = "namespace Fake" + fakeClass.getName() + "Namespace {\n"; 
		String fakeNamespaceStr = "";
		
		for( ICPPASTFunctionDefinition i : fakeClassFunction ) {
			ICPPASTFunctionDeclarator functionDeclarator = ( ICPPASTFunctionDeclarator ) i.getDeclarator();
			IBinding binding = functionDeclarator.getName().resolveBinding();	
			ICPPFunction function = ( ICPPFunction ) binding;

			String functionName = function.getName();
			print.println( functionName );

			if ( function instanceof ICPPConstructor ) {
				getFakeConstructorParameter( i, constructorFakeParameterDeclSpecifier, constructorFakeParameter, includes );
				
				for ( int j = 0; j < constructorFakeParameter.size(); j++ ) {
					if ( constructorFakeParameterDeclSpecifier.get( j ).contains( "Ptr" ) ) {
						consFakeStr.add( "Fake" + fakeClass.getName() + "Namespace::" + constructorFakeParameter.get( j ) );
						fakeClassNamespaceStr += "static " + constructorFakeParameterDeclSpecifier.get( j ) + " " + constructorFakeParameter.get( j ) + "( new " + constructorFakeParameterDeclSpecifier.get( j ).replace( "Ptr", "" ) + "() );\n";
					} else {
						consFakeStr.add( constructorFakeParameter.get( j ) );
						fakeNamespaceStr += constructorFakeParameterDeclSpecifier.get( j ) + " " + constructorFakeParameter.get( j ) + ";\n";
					}
				}
				
			} else if ( functionName.contains( "~" ) ) {
				continue;
			} else {			
				fakeFunctionDeclarator.add( functionDeclarator );
				fakeFunctionStr.add( "fake_" + function.getName() + "( \"Fake" + fakeClass.getName() + "::" + function.getName() + "\" )" );
			}
		}
		
		for ( String i : includes ) {					
			includeStr += "#include \"" + i + "\"\n";			
		}

		fakeClassNamespaceStr += "} // namespace Fake" + fakeClass.getName() + "Namespace\n\n";
		
		cons += includeStr + "\n"
				+ fakeClassNamespaceStr
				+ "namespace " + fakeClass.getScope() + " {\n\n"
				+ fakeNamespaceStr
				+ "\nFake" + fakeClass.getName() + "::Fake" + fakeClass.getName() + "()\n"		
				+ ": " + fakeClass.getName() + "( "
				+ String.join( ",\n", consFakeStr ) + " )\n, "
				+ String.join( "\n, ", fakeFunctionStr )
				+ "\n{}\n\n"
				+ "Fake" + fakeClass.getName() + "::~Fake" + fakeClass.getName() + "()\n{}\n\n";
		
//		cons = "void constructor(){return 0;}\n";
		
		return cons;
	}

	private void getFakeConstructorParameter( ICPPASTFunctionDefinition functionDefn, List<String> constructorFakeParameterDeclSpecifier,
			List<String> constructorFakeParameter, List<String> includes ) throws CoreException, InterruptedException, DOMException {
		ICPPASTFunctionDeclarator functionDeclarator = ( ICPPASTFunctionDeclarator ) functionDefn.getDeclarator();
		IBinding binding = functionDefn.getDeclarator().getName().resolveBinding();	
		ICPPConstructor constructor = ( ICPPConstructor ) binding;

		for ( ICPPASTParameterDeclaration memberParameter : functionDeclarator.getParameters() ) {
			if ( memberParameter.getDeclSpecifier() instanceof ICPPASTNamedTypeSpecifier ) { 
				String parameterDeclarator = memberParameter.getDeclarator().getName().toString();
				String parameterDeclSpecifier = memberParameter.getDeclSpecifier().toString().replaceAll( ".* ", "" );

				//			constructorParameter.add( parameterDeclarator );
				//			constructorParameterDeclSpecifier.add( parameterDeclSpecifier.replaceAll( ".*::", "" ) );

				String fakeParameterDeclSpecifier = "Fake" + parameterDeclSpecifier.replaceFirst( ".*::", "" ).replace( "Ptr", "" );
				String fake_parameterDeclSpecifier = "Fake_" + parameterDeclSpecifier.replaceFirst( ".*::", "" ).replace( "Ptr", "" );

				String parameterIncludes = astVisitorImpl.findFakeIncludes( memberParameter.getDeclSpecifier()/*parameterDeclSpecifier.replace( "Ptr", "" )*/ );									
				if ( parameterIncludes.contains( fakeParameterDeclSpecifier ) ) {
					//				print.println( fakeParameterDeclSpecifier + " " + parameterIncludes );

					if ( parameterDeclSpecifier.contains( "::" ) ) {
						parameterDeclSpecifier = parameterDeclSpecifier.replace( "::", "::Fake" );
					} else {
						parameterDeclSpecifier = "Fake" + parameterDeclSpecifier;
					}						
				} else if ( parameterIncludes.contains( fake_parameterDeclSpecifier ) ) {
					//				print.println( fakeParameterDeclSpecifier + " " + parameterIncludes );

					if ( parameterDeclSpecifier.contains( "::" ) ) {
						parameterDeclSpecifier = parameterDeclSpecifier.replace( "::", "::Fake_" );
					} else {
						parameterDeclSpecifier = "Fake_" + parameterDeclSpecifier;
					}
				}
				includes.add( parameterIncludes );
				parameterDeclarator = "fake" + parameterDeclarator.substring( 0, 1 ).toUpperCase() + parameterDeclarator.replace( "In", "" ).substring( 1 );

				print.println( parameterDeclSpecifier + " " + parameterDeclarator + " " + parameterIncludes );

				constructorFakeParameter.add( parameterDeclarator );
				constructorFakeParameterDeclSpecifier.add( parameterDeclSpecifier );
			}
		}
	}
	
	private String printMethodWatcher() {
		String methodWatcher = "void Fake" + fakeClass.getName() + "::registerMethodWatcher( TestUtility::FakeMethodWatcher& methodWatcher )\n{\n";
		for( ICPPASTFunctionDeclarator i : fakeFunctionDeclarator ) {
			IBinding binding = i.getName().resolveBinding();	
			methodWatcher += "  fake_" + binding.getName() + ".registerMethodWatcher( methodWatcher );\n";			
		}
		methodWatcher += "}\n\n";
		return methodWatcher;
	}

	private String printMethodNotCalled() {
		String methodNotCalled = "void Fake" + fakeClass.getName() + "::verifyFakeMethodWasNotCalled( const std::string& testCondition )\n{\n";
		for( ICPPASTFunctionDeclarator i : fakeFunctionDeclarator ) {
			IBinding binding = i.getName().resolveBinding();	
			methodNotCalled += "  TestUtility::verifyFakeMethodWasNotCalled( fake_" + binding.getName() + ", testCondition );\n";			
		}
		methodNotCalled += "}\n\n";
		return methodNotCalled;
	}

	private String printMethodUsage() {
		String methodUsage = "void Fake" + fakeClass.getName() + "::verifyFakeMethodUsage( const std::string& testCondition )\n{\n";
		for( ICPPASTFunctionDeclarator i : fakeFunctionDeclarator ) {
			IBinding binding = i.getName().resolveBinding();	
			methodUsage += "  TestUtility::verifyFakeMethodUsage( fake_" + binding.getName() + ", testCondition );\n";			
		}
		methodUsage += "}\n\n";
		return methodUsage;
	}

	private String printResetMethodUsage() {
		String resetMethodUsage = "void Fake" + fakeClass.getName() + "::resetFakeMethodUsage( void )\n{\n";
		for( ICPPASTFunctionDeclarator i : fakeFunctionDeclarator ) {
			IBinding binding = i.getName().resolveBinding();	
			resetMethodUsage += "  fake_" + binding.getName() + ".reset();\n";			
		}
		resetMethodUsage += "}\n\n";
		return resetMethodUsage;
	}

	private String printFakeFunction() throws DOMException {
		String fakeFunctionStr = "";

		for( ICPPASTFunctionDefinition i : fakeClassFunction ) {	
			ICPPASTFunctionDeclarator functionDeclarator = ( ICPPASTFunctionDeclarator ) i.getDeclarator();
			IBinding binding = functionDeclarator.getName().resolveBinding();	
			ICPPFunction function = ( ICPPFunction ) binding;
			
			String functionName = function.getName();
			if ( function instanceof ICPPConstructor || functionName.contains( "~" ) ) {
				continue;
			}

			ArrayList functionParameter = new ArrayList<String>();
			
/*			for ( ICPPASTConstructorChainInitializer memberInitializer : functionDefinition.getMemberInitializers() ) {
				IASTName memberName = memberInitializer.getMemberInitializerId();
				print.println( memberName.toString() );
			}	
*/
			for ( ICPPASTParameterDeclaration memberParameter : functionDeclarator.getParameters() ) {
//				IASTName memberName = memberInitializer.get;
				functionParameter.add( memberParameter.getDeclarator().getName().toString() );
//				print_cpp.println( memberParameter.getRawSignature() + " " + memberParameter.getDeclSpecifier().getRawSignature() + " " + memberParameter.getDeclarator().getRawSignature() );
			}
			fakeFunctionStr += i.getDeclSpecifier() + " Fake" + i.getDeclarator().getRawSignature() + "\n{\n"
					+ "  return fake_" + function.getName() + "( " + String.join( ", ", functionParameter ) + " );\n}\n\n";
		}
		fakeFunctionStr += "} // namespace " + fakeClass.getScope() + "\n";
		return fakeFunctionStr;
	}
}
