package fakeunittest.actions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.index.IndexFilter;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

public class FakeClassTest {

	private List<ICPPASTFunctionDefinition> fakeClassFunction;
	private List<ICPPASTFunctionDefinition> fakeClassFunction_h;
	private ASTVisitorImpl astVisitorImpl;
	private IBinding fakeClass;
	private List<String> constructorParameterDeclSpecifier;
	private List<String> constructorParameter;
	private List<String> constructorFakeParameterDeclSpecifier;
	private List<String> constructorFakeParameter;
	private String fakeSource;
	private String projectStr;
	private PrintStream print;

	/**
	 * The constructor
	 * @throws CoreException 
	 */
	public FakeClassTest( ASTVisitorImpl astVisitorImplIn, PrintStream printIn, String projectIn, String fakeSourceIn )
					throws CoreException {
		astVisitorImpl = astVisitorImplIn;
		fakeClassFunction = astVisitorImpl.getFakeClass();
		fakeClassFunction_h = astVisitorImpl.getFakeClass_h();
		ICPPASTFunctionDefinition functionDefinition = ( ICPPASTFunctionDefinition ) fakeClassFunction.get( 0 );
		IBinding binding = functionDefinition.getDeclarator().getName().resolveBinding();	
		fakeClass = binding.getOwner();

		constructorParameterDeclSpecifier = new ArrayList<String>();
		constructorParameter = new ArrayList<String>();
		constructorFakeParameterDeclSpecifier = new ArrayList<String>();
		constructorFakeParameter = new ArrayList<String>();
		print = printIn;
		projectStr = projectIn;
		fakeSource = fakeSourceIn;
	}

	void generateFakeClassTest() {
		print.println( "generateFakeClassTest" );
	}

	void printFunction( IASTTranslationUnit tu )
			throws CoreException, IOException, DOMException, InterruptedException {			
		print.println( "" + fakeSource.replaceAll( "src", "unittest" ).replaceFirst( ".cpp", "Test-debug.cpp" ) );

/*		File test_cpp = new File( fakeSourceFile.getParent().replaceAll( "src", "unittest" ) + "/" + fakeClass.getName() + "Test-debug.cpp" );
		PrintStream printTest_cpp = new PrintStream( test_cpp );

		printTest_cpp.println( printInclude( tu ) 
				+ printCons()
				+ printVerifyFakeMethodUsage()
				+ printTestValidConstructor()
				+ printTestConstructorParametersGet() );

		printTest_cpp.close();
*/	
		IPath path_cpp = Path.fromOSString( "" + fakeSource.replaceAll( "src", "unittest" ).replaceFirst( ".cpp", "Test-debug.cpp" ) );
		print.println( path_cpp.toOSString() );
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject( projectStr );
		IFile file_cpp = project.getFile( path_cpp );
		
		InputStream file_stream = new ByteArrayInputStream( ( printCons( tu )
				+ printVerifyFakeMethodUsage()
				+ printTestValidConstructor()
				+ printTestConstructorParametersGet() ).getBytes() );
		
		if ( file_cpp.exists() ) {
			file_cpp.setContents( file_stream, true, true, null );
		} else {
			file_cpp.create( file_stream , true, null );
		}
		file_stream.close();		
	}

	private String printCons( IASTTranslationUnit tu ) throws DOMException, CoreException, InterruptedException {
		String cons;
		String includeStr = "";
		List<String> includes = new ArrayList<String>();
		
		cons = "using namespace " + fakeClass.getScope() + ";\n\n"
				+ "namespace tut {\n\n"
				+ "struct " + fakeClass.getName() + "_data\n{\n";

		for ( IASTPreprocessorIncludeStatement include : tu.getIncludeDirectives() ) {					
			if ( include.getPath().contains( fakeClass.getName() ) ) {
				includeStr += include + "\n\n";
			}
		}		

		for( ICPPASTFunctionDefinition i : fakeClassFunction ) {	
			ICPPASTFunctionDeclarator functionDeclarator = ( ICPPASTFunctionDeclarator ) i.getDeclarator();
			IBinding binding = i.getDeclarator().getName().resolveBinding();	
			ICPPFunction function = ( ICPPFunction ) binding;

			String functionName = function.getName();
			if ( function instanceof ICPPConstructor ) {
				getFakeConstructorParameter( i, constructorFakeParameterDeclSpecifier, constructorFakeParameter, includes );

				//				test_cpp.println( String.join( ", ", constructorParameter ) );
				//				test_cpp.println( String.join( ", ", constructorParameterDeclSpecifier ) );

				for ( int j = 0; j < constructorFakeParameter.size(); j++ ) {
					cons += "  " + constructorFakeParameterDeclSpecifier.get( j ) + " " + constructorFakeParameter.get( j ) + ";\n";					
				}

				cons += "\n  EventStatus failureEventStatus;\n  EventStatus goodEventStatus;\n\n"
						+ "  " + fakeClass.getName() + "_data()\n  : ";
				for ( int j = 0; j < constructorFakeParameter.size(); j++ ) {
					if ( constructorFakeParameterDeclSpecifier.get( j ).contains( "Ptr" ) ) {
						cons += constructorFakeParameter.get( j ) + "( new " + constructorFakeParameterDeclSpecifier.get( j ).replace( "Ptr", "" ) + "() )\n  , ";					
					} else {
						cons += constructorFakeParameter.get( j ) + "()\n  , ";					
					}
				}

				cons += "failureEventStatus( FakeEvt::failure )\n  , goodEventStatus()\n  {\n  }\n\n";				  

				/*				for ( ICPPASTConstructorChainInitializer memberInitializer : i.getMemberInitializers() ) {
					IASTName memberName = memberInitializer.getMemberInitializerId();
					test_cpp.println( memberInitializer + " " + memberName.toString() );
				}				
				 */
				break;
			}
		}
		
		for ( String i : includes ) {					
			includeStr += "#include \"" + i + "\"\n";			
		}

		includeStr += "#include \"framework/FakeEventList.h\"\n\n"
				+ "#include <tut.h>\n\n";
		
		cons += "  ~" + fakeClass.getName() + "_data()\n  {\n  }\n\n"; 
		return includeStr + cons;
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

				constructorParameter.add( parameterDeclarator );
				constructorParameterDeclSpecifier.add( parameterDeclSpecifier.replaceAll( ".*::", "" ) );

				String fakeParameterDeclSpecifier = "Fake" + parameterDeclSpecifier.replaceFirst( ".*::", "" ).replace( "Ptr", "" );
				String fake_parameterDeclSpecifier = "Fake_" + parameterDeclSpecifier.replaceFirst( ".*::", "" ).replace( "Ptr", "" );

				String parameterIncludes = astVisitorImpl.findFakeIncludes( memberParameter.getDeclSpecifier()/*parameterDeclSpecifier.replace( "Ptr", "" )*/ );									
				print.println( parameterDeclSpecifier + " " + parameterIncludes );
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

				constructorFakeParameter.add( parameterDeclarator );
				constructorFakeParameterDeclSpecifier.add( parameterDeclSpecifier );
			}
		}
	}
	
	private String printVerifyFakeMethodUsage() {
		String verifyFakeMethodUsage;
		verifyFakeMethodUsage = "  void verifyFakeMethodUsage( const std::string& testCondition )\n  {\n";
		for ( int j = 0; j < constructorFakeParameter.size(); j++ ) {
			verifyFakeMethodUsage += "    " + constructorFakeParameter.get( j ) + "->verifyFakeMethodUsage( testCondition );\n";					
		}
		verifyFakeMethodUsage += "  }\n\n};\n\n";		
		return verifyFakeMethodUsage;
	}
	
	private String printTestValidConstructor() throws DOMException {		
		return "typedef test_group<" + fakeClass.getName() + "_data> tg;\n"
				+ "typedef tg::object object;\n"
				+ "tg " + fakeClass.getName() + "_group( \"" + fakeClass.getScope() + "::" + fakeClass.getName() + "\" );\n\n"
				+ "// =================================================\n"
				+ "// Constructor\n"
				+ "// =================================================\n"
				+ "\n"
				+ "/**\n"
				+ " * Test that the constructor works properly when passed valid arguments.\n"
				+ " */\n"
				+ "template<>\n"
				+ "template<>\n"
				+ "void object::test<1>()\n{\n"
				+ "  " + fakeClass.getName() + " " + fakeClass.getName().substring( 0, 1 ).toLowerCase() + fakeClass.getName().substring( 1 ) + "( "
				+ String.join( ", ", constructorFakeParameter ) + " );\n"
				+ "  ensure( \"the constructor works properly when passed valid arguments\", true );\n}\n\n";
	}

	private String printTestConstructorParametersGet() {
		String testConstructorParametersGet;
		testConstructorParametersGet = "/**\n"
				+ " * Test that the " + String.join( " and ", constructorParameterDeclSpecifier )
				+ " passed to the constructor\n"
				+ " * is returned by the getter.\n"
				+ " */\n"
				+ "template<>\n"
				+ "template<>\n"
				+ "void object::test<2>()\n{\n"
				+ "  " + fakeClass.getName() + " " + fakeClass.getName().toLowerCase() + "( "
				+ String.join( ", ", constructorFakeParameter ) + " );\n\n";
		
		for ( int j = 0; j < constructorParameterDeclSpecifier.size(); j++ ) {
			String parameterDeclSpecifier = constructorParameterDeclSpecifier.get( j ).toString().replaceAll( ".*:", "" ).replace( "Ptr", "" );
			testConstructorParametersGet += "  ensure_equals( \"the " + parameterDeclSpecifier
					+ " passed to the constructor \n"
					+ "                  \"is returned by the getter.\",\n"
					+ "                  " + fakeClass.getName().toLowerCase() + ".get" + parameterDeclSpecifier + "(),\n"
					+ "                  " + constructorFakeParameter.get( j ) + " );\n\n";
		}					
		testConstructorParametersGet += "}\n\n} // namespace tut\n";		
		return testConstructorParametersGet;
	}
}
