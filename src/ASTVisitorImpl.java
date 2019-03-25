package fakeunittest.actions;

import java.awt.List;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IScope;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPFunction;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexBinding;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.index.IndexFilter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

public class ASTVisitorImpl extends ASTVisitor {
	private PrintStream print;
	private ArrayList<ICPPASTFunctionDefinition> fakeClass;
	private ArrayList<IASTSimpleDeclaration> fakeClassSimpleDecl;	
	private ArrayList<ICPPASTFunctionDefinition> fakeClass_h;
	
	ASTVisitorImpl( PrintStream printIn ) {
		shouldVisitDeclarations = true;
//		shouldVisitNamespaces = true;
//		shouldVisitNames = true;
//		shouldVisitExpressions = true;
		print = printIn;
		fakeClass = new ArrayList<ICPPASTFunctionDefinition>();//new HashMap<IBinding, Set<ICPPASTFunctionDefinition>>();
		fakeClass_h = new ArrayList<ICPPASTFunctionDefinition>();
		fakeClassSimpleDecl = new ArrayList<IASTSimpleDeclaration>();
	}

	public ArrayList<ICPPASTFunctionDefinition> getFakeClass() {
		return fakeClass;
	}
	
	public ArrayList<ICPPASTFunctionDefinition> getFakeClass_h() {
		return fakeClass_h;
	}
	
	public ArrayList<IASTSimpleDeclaration> getFakeClassSimpleDecl() {
		return fakeClassSimpleDecl;
	}
	
	public int visit( IASTDeclaration declaration ) {
		print.println( "visit " + declaration.getClass().getName() + " " + declaration.getRawSignature() );
		if ( declaration instanceof ICPPASTFunctionDefinition ) {
			
			print.println( "declaration " + declaration.getClass().getName() + " " + declaration.getRawSignature() );
			
			ICPPASTFunctionDefinition functionDefinition = ( ICPPASTFunctionDefinition ) declaration;
			IBinding binding = functionDefinition.getDeclarator().getName().resolveBinding();	
			ICPPFunction function = ( ICPPFunction ) binding;
			
			if ( declaration.getContainingFilename().contains( function.getOwner().getName() ) ) {				
				print.println(declaration.getContainingFilename());
				print.println( functionDefinition );
				if ( declaration.getContainingFilename().contains( ".cpp" ) ) {
					print.println( "add" );
					fakeClass.add( functionDefinition );
				} else {
					print.println( "add h" );
					fakeClass_h.add( functionDefinition );
				}
			}
				
			//			String functionName = function.getName();					
			//			if ( ! ( function instanceof ICPPConstructor || functionName.contains( "~" ) ) ) {
//			addFunctionDefinition( functionDefinition );
			//		}
			//			}
		} 

		if ( declaration instanceof IASTSimpleDeclaration ) {
			IASTSimpleDeclaration simpleDeclaration = ( IASTSimpleDeclaration ) declaration;
			print.println( "declaration " + declaration.getClass().getName() + " " + declaration.getRawSignature() );
			fakeClassSimpleDecl.add( simpleDeclaration );
		}
		//		}
		return PROCESS_CONTINUE;
	}	

	public String findIncludes( IASTDeclSpecifier decl ) throws CoreException, InterruptedException, DOMException {
//		print.println( decl.getClass().getName() );
		IScope parameterScope;
		String declStr;
		
		if ( decl.toString().contains( "::" ) ) {
			declStr = decl.toString().replaceFirst( ".*::", "" ).replace( "Ptr", "" ).replaceFirst( ".* ", "" );
		} else {
			declStr = decl.toString().replace( "Ptr", "" ).replaceFirst( ".* ", "" );
		}

		String defn = "";

		if ( decl instanceof ICPPASTNamedTypeSpecifier ) {
			ICPPASTNamedTypeSpecifier parameterTypeSpecifier = ( ICPPASTNamedTypeSpecifier ) decl;
			IASTName parameterClassName = ( IASTName ) parameterTypeSpecifier.getName();
			parameterScope = parameterClassName.resolveBinding().getScope();
		} else {
			return defn;
		}
		
		print.println( "\n\n" + decl + " " + parameterScope + " " + declStr );

		IIndex index = decl.getTranslationUnit().getIndex();
		// find references for each binding
		index.acquireReadLock();

		if ( parameterScope.toString().contains( "global scope") ) {			
			for ( IIndexBinding b : index.findBindings( declStr.toCharArray(), IndexFilter.ALL, new NullProgressMonitor() ) ) {				
//				if ( b instanceof ICPPClassType ) {
				IIndexName[] names= index.findDefinitions( b );
				for ( IIndexName n : names ) {
					IASTFileLocation fileLoc= n.getFileLocation();					
					defn = fileLoc.getFileName().replaceFirst( ".*src\\\\", "" ).replaceAll( "\\\\", "/" );
					if ( /*defn.contains( declStr ) &&*/ !defn.contains( "cygwin" ) ) {
						print.println( defn + " at offset " + fileLoc.getNodeOffset() );
						return defn;
					}
				}
//				}
			}
		} else {
			for ( IBinding b1 : parameterScope.find( declStr ) ) {
				for ( IIndexName n : index.findDefinitions( b1 ) ) {
					IASTFileLocation fileLoc= n.getFileLocation();
					defn = fileLoc.getFileName().replaceFirst( ".*src\\\\", "" ).replaceAll( "\\\\", "/" );
					if ( /*defn.contains( declStr ) &&*/ !defn.contains( "cygwin" ) ) {
						print.println( defn + " at offset " + fileLoc.getNodeOffset() );
						return defn;
					}
				}
			}
		}
		index.releaseReadLock();
		return "ERROR " + decl.toString();
	}


	public String findFakeIncludes( IASTDeclSpecifier decl ) throws CoreException, InterruptedException, DOMException {		
		ICPPASTNamedTypeSpecifier parameterTypeSpecifier = ( ICPPASTNamedTypeSpecifier ) decl;
		IASTName parameterClassName = ( IASTName ) parameterTypeSpecifier.getName();
		IScope parameterScope = parameterClassName.resolveBinding().getScope();
		String declStr;
		
		if ( decl.toString().contains( "::" ) ) {
			declStr = decl.toString().replaceFirst( ".*::", "" ).replace( "Ptr", "" ).replaceFirst( ".* ", "" );
		} else {
			declStr = decl.toString().replace( "Ptr", "" ).replaceFirst( ".* ", "" );
		}
//		print.println( "\n\n" + decl + " " + parameterClassName.resolveBinding().getScope() + " " + declStr );
		
		String defn;

		IIndex index = decl.getTranslationUnit().getIndex();
		// find references for each binding
		index.acquireReadLock();

		if ( parameterScope.toString().contains( "global scope") ) {			
			IIndexBinding [] b_namespace;			
			b_namespace = index.findBindings( ( "Fake" + declStr ).toCharArray(), IndexFilter.ALL, new NullProgressMonitor() );

			if ( b_namespace.length == 0 ) {				
				b_namespace = index.findBindings( ( "Fake_" + declStr ).toCharArray(), IndexFilter.ALL, new NullProgressMonitor() );
			}

			for ( IIndexBinding b : b_namespace ) {				
//				if ( b instanceof ICPPClassType ) {
				IIndexName[] names= index.findDefinitions( b );
				for ( IIndexName n : names ) {
					IASTFileLocation fileLoc= n.getFileLocation();
//					print.println( fileLoc.getFileName() + " at offset " + fileLoc.getNodeOffset() );
					defn = fileLoc.getFileName().replaceFirst( ".*unittest\\\\", "" ).replaceAll( "\\\\", "/" );
					if ( defn.contains( declStr ) ) {
						return defn;
					}
				}
//				}
			}
		} else {
			IBinding [] b_namespace;
			b_namespace = parameterScope.find( "Fake" + declStr );

			if ( b_namespace.length == 0 ) {
				b_namespace = parameterScope.find( "Fake_" + declStr );
			}

			for ( IBinding b1 : b_namespace ) {
				for ( IIndexName n : index.findDefinitions( b1 ) ) {
					IASTFileLocation fileLoc= n.getFileLocation();
//					print.println( n.toString() + " " + fileLoc.getFileName() + " at offset " + fileLoc.getNodeOffset() );
					defn = fileLoc.getFileName().replaceFirst( ".*unittest\\\\", "" ).replaceAll( "\\\\", "/" );
					if ( defn.contains( declStr ) ) {
						return defn;
					}
				}
			}
		}
		index.releaseReadLock();
		return "ERROR " + decl.toString();
	}
	
	
	
	
}
