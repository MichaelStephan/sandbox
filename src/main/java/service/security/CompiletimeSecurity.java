package service.security;

import org.codehaus.groovy.control.customizers.SecureASTCustomizer;

import java.security.AccessController;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by i303874 on 12/24/14.
 */
public class CompiletimeSecurity extends SecureASTCustomizer {
    public CompiletimeSecurity() {
        super.setClosuresAllowed(true);
        super.setIndirectImportCheckEnabled(false);
        super.setMethodDefinitionAllowed(true);
        super.setPackageAllowed(false);

        // whitelists
//        super.setConstantTypesClassesWhiteList(Collections.emptyList());
//        super.setConstantTypesWhiteList(Collections.emptyList());
//        super.setExpressionsWhitelist(Collections.emptyList());
//        super.setImportsWhitelist(Collections.emptyList());
//        super.setReceiversClassesWhiteList(Arrays.asList(new Class[]{Permissions.class, AccessController.class}));
//        super.setReceiversWhiteList(Collections.emptyList());
//        super.setStarImportsWhitelist(Collections.emptyList());
//        super.setStatementsWhitelist(Collections.emptyList());
//        super.setStaticImportsWhitelist(Collections.emptyList());
//        super.setTokensWhitelist(Collections.emptyList());

        // blacklists
        super.setConstantTypesClassesBlackList(Collections.emptyList());
        super.setConstantTypesBlackList(Collections.emptyList());
        super.setExpressionsBlacklist(Collections.emptyList());
        super.setImportsBlacklist(Arrays.asList(new String[]{"java.security.AccessController"}));
        super.setReceiversClassesBlackList(Arrays.asList(new Class[]{AccessController.class, Class.class}));
//        super.setReceiversBlackList(Collections.emptyList());
        super.setStarImportsBlacklist(Arrays.asList(new String[]{"java.lang.reflect.*"}));
        super.setStatementsBlacklist(Collections.emptyList());
        super.setStaticImportsBlacklist(Arrays.asList());
        super.setTokensBlacklist(Collections.emptyList());
    }
}
