print 'lala'
//System.exit(1)

//import java.security.*
//import java.security.cert.Certificate

//import java.security.*
//import java.lang.reflect.*;

//def Permissions perms = new Permissions();
//perms.add(new RuntimePermission("accessDeclaredMembers"));
//perms.add(new RuntimePermission("exitVM.1"))
//def ProtectionDomain[] domains = [new ProtectionDomain(new CodeSource(null, (Certificate[]) null), perms)]
//def AccessControlContext context = new AccessControlContext(domains)
//def PrivilegedAction privilegedAction = new PrivilegedAction() {
//    public Object run() {
//        System.exit(1)
//    }
//}
//AccessController.doPrivileged(privilegedAction, context)

//class1 = Class.forName("java.security.AccessController");
//Method method = class1.getMethod("doPrivileged", PrivilegedAction.class);
//Object o = method.invoke(null, privilegedAction);

//println "ls".execute().text

//
//import client.ProductServiceClient
//import com.google.common.util.concurrent.FutureCallback
//import com.google.common.util.concurrent.Futures
//
//import javax.ws.rs.core.Response
//

//new PriceServiceClient().getProducts(accessToken, "tuoqhujokspp", null).get()
//
//def response = new ProductServiceClient().getProducts(accessToken, "Ts2JQ7UA348FVkMKMh69Mbd7KgPN", null).get()
//switch (response.status) {
//    case 200:
//        def products = response.readEntity(List.class)
//        print products
//
//    default:
//        println """ fetching procuts failed with ${response.status}: ${response.readEntity(String.class)}"""
//        break;
//}
//System.exit(1)
//
//scriptShell = new GroovyShell(new CompilerConfiguration().addCompilationCustomizers())
//Script script = scriptShell.parse("""
//import java.security.*
//import java.security.cert.Certificate
//def Permissions perms = new Permissions();
//perms.add(new AllPermission())
//def ProtectionDomain[] domains = [new ProtectionDomain(new CodeSource(null, (Certificate[]) null), perms)]
//def AccessControlContext context = new AccessControlContext(domains)
//AccessController.doPrivileged(new PrivilegedAction() {
//    public Object run() {
//        System.exit(1)
//    }
//}, context)
//""");
//script.run()
