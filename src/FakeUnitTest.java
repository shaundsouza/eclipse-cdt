package fakeunittest.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.cdt.core.dom.ast.IASTAttributeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit.IDependencyTree;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit.IDependencyTree.IASTInclusionNode;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.browser.TypeUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.dom.ast.IASTBreakStatement;
import org.eclipse.text.edits.TextEditGroup;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAttribute;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNamespace;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.FileDialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class FakeUnitTest implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public FakeUnitTest() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		
		try {
		    runtest();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DOMException e) {
			e.printStackTrace();
		}
		
/*		MessageDialog.openInformation(
				window.getShell(),
				"Fakeunittest",
				"Generated Fake Unit Test");
*/
	}
		
	void runtest() throws CoreException, InterruptedException, IOException, DOMException {
	    IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
	    
	    Display d = Display.getDefault();
	    Shell s = new Shell( d );
	    // s.open();
	    FileDialog dialog = new FileDialog( s, SWT.OPEN );
	    // dialog.setFilterExtensions(new String [] {"*.html"});
	    dialog.setFilterPath( "c:\\eclipse\\" );

	    File fakeSources = new File( dialog.open() );	    
//	    String project = fakeSources.getParentFile().getName();
	    
	    List<String> fakeSourcesList = Files.readAllLines( Paths.get( fakeSources.toString() ) );
	    String project = fakeSourcesList.get(0).replaceAll( "#", "" ).trim();
	    
/*		IEditorPart e= PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		System.out.println(e.getEditorInput().toString());
		IPath path= Path.fromOSString(e.getEditorInput().toString());
	
		IFile file1= ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		String project = file1.getParent().getName();
*/		
		ICProject cppProject = CoreModel.getDefault().getCModel().getCProject( project );   	
		IIndex index = CCorePlugin.getIndexManager().getIndex( cppProject );

/*		System.out.println(cppProject.getLocationURI().getPath());
		System.out.println(file1.getFullPath().toString().replaceFirst( ".*"+project , "").replace(")",""));
		File fakeSources = new File( cppProject.getLocationURI().getPath() + file1.getFullPath().toString().replaceFirst( ".*"+project , "").replace(")","") );
*/	
	    String projectPath = fakeSources.getParent();

	    File file = new File( projectPath + "/debug.log" );   
		PrintStream print;
	    print = new PrintStream( file );	   

		print.println( "cppProject " + cppProject.toString() + " " + cppProject.getPath().toString() );		

		print.println( projects.length );
	    print.println( project + " " + projectPath );	    
				
	    for( String fakeSource : fakeSourcesList/*Files.readAllLines( Paths.get( fakeSources.toString() ) )*/ ) {
				
	    	if( !fakeSource.startsWith("#") && !fakeSource.isEmpty() ) {
				print.println(fakeSources.getParentFile().getAbsolutePath());

				File fakeSourceFile = new File( fakeSources.getParentFile().getAbsolutePath() + fakeSource );

				IPath path_cpp = Path.fromOSString( fakeSource );
				print.println(path_cpp.toOSString());
				ICElement iceElement_cpp = cppProject.findElement( path_cpp );				
				ITranslationUnit translationUnit_cpp = ( ITranslationUnit ) iceElement_cpp.getAdapter( ITranslationUnit.class );
						
				ASTVisitorImpl astVisitorImpl = new ASTVisitorImpl( print );
				IASTTranslationUnit tu_cpp, tu_h;
				
	    		index.acquireReadLock();
	    		try {
	    			tu_cpp = translationUnit_cpp.getAST( index, ITranslationUnit.AST_SKIP_ALL_HEADERS | ITranslationUnit.AST_SKIP_FUNCTION_BODIES );
	    			if ( tu_cpp != null ) {
//	    				printFakeTree(tu,0,print);	    				
	    				tu_cpp.accept( astVisitorImpl );
	    			}
	    		} finally {
	    			index.releaseReadLock();
//	    			tu_cpp = null;
	    		}	    		

/*	    		IPath path_h = Path.fromOSString( project + "/insightdiags/" + fakeSource.replace( ".cpp" , ".h" ) );
	    		print.println( path_h.toString() );
	    		IFile file_h= ResourcesPlugin.getWorkspace().getRoot().getFile( path_h );   
	    		ITranslationUnit translationUnit_h = ( ITranslationUnit ) CoreModel.getDefault().create( file_h );
*/
				IPath path_h = Path.fromOSString( fakeSource.replace( ".cpp" , ".h" ) );
				print.println(path_h.toOSString());
				ICElement iceElement_h = cppProject.findElement( path_h );			
				ITranslationUnit translationUnit_h = ( ITranslationUnit ) iceElement_h.getAdapter( ITranslationUnit.class );

	    		index.acquireReadLock();
	    		try {
	    			tu_h = translationUnit_h.getAST( index, ITranslationUnit.AST_SKIP_ALL_HEADERS | ITranslationUnit.AST_SKIP_FUNCTION_BODIES );
	    			if ( tu_h != null ) {
//	    				printFakeTree(tu,0,print);
//	    				printFunction_h( tu, fakeSourceFile, print );
	    				tu_h.accept( astVisitorImpl );
	    			}
	    		} finally {
	    			index.releaseReadLock();
//	    			tu_h = null;
	    		}	    		
	    		
	    		List<ICPPASTFunctionDefinition> fakeClass = astVisitorImpl.getFakeClass();//new HashMap<IBinding, Set<ICPPASTFunctionDefinition>>();
	    		List<ICPPASTFunctionDefinition> fakeClass_h = astVisitorImpl.getFakeClass_h();
				
				ClassFake_cpp classFake_cpp = new ClassFake_cpp( astVisitorImpl, print, project, fakeSource );
				classFake_cpp.generateClassFake();	    			   
				classFake_cpp.printFunction();
				
				ClassFake_h classFake_h = new ClassFake_h( astVisitorImpl, print, project, fakeSource );
				classFake_h.generateClassFake();	    			   
				classFake_h.printFunction( tu_h );

				FakeClassTest fakeClassTest = new FakeClassTest( astVisitorImpl, print, project, fakeSource );
				fakeClassTest.generateFakeClassTest();	    			   
				fakeClassTest.printFunction( tu_cpp );

	    		print.close();
	    	}
	    }	    
	}
	
//	ICElement ice = CoreModel.getDefault().create(file);
//	ITranslationUnit tu= (ITranslationUnit) ice;
	
//	http://git.eclipse.org/c/cdt/org.eclipse.cdt.git/tree/codan/org.eclipse.cdt.codan.checkers/src/org/eclipse/cdt/codan/internal/checkers/ClassMembersInitializationChecker.java
//	http://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.cdt.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fcdt%2Fcore%2Fdom%2Fast%2Fcpp%2FICPPASTFunctionDefinition.html

	private void printFakeTree( IASTNode node, int index, PrintStream print )
			throws CoreException {
		IASTNode[] children = node.getChildren();
		if( node instanceof ICPPASTFunctionDefinition ) {
			ICPPASTFunctionDefinition func1 = ( ICPPASTFunctionDefinition ) node;
			
//			isConstructor(func1,print);
//			return;
		}
		
 		for( int i = 0; i < index; i++ )
			print.print( "  " );			

		print.print( node.getClass().getName() );
		if( node.getRawSignature().length() < 100 )
			print.print( " " + node.getRawSignature() );
		print.println();
		
		for ( IASTNode node1 : children )			
			printFakeTree( node1, index+1, print );
	}
	
	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}