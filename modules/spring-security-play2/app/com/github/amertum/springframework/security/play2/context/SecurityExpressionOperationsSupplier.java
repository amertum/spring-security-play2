package com.github.amertum.springframework.security.play2.context;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.DenyAllPermissionEvaluator;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.hierarchicalroles.NullRoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.function.Supplier;

public class SecurityExpressionOperationsSupplier
        implements Supplier<SecurityExpressionOperations> {

    public SecurityExpressionOperations get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            authentication = HttpSessionSecurityContextRepository.createAnonymousAuthenticationToken();
        }

        final SecurityExpressionRoot expressionOperations = new SecurityExpressionRoot(authentication) {};
        expressionOperations.setPermissionEvaluator(permissionEvaluator);
        expressionOperations.setRoleHierarchy(roleHierarchy);
        expressionOperations.setTrustResolver(authenticationTrustResolver);

        return expressionOperations;
    }

    public void setPermissionEvaluator(PermissionEvaluator permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    public void setRoleHierarchy(RoleHierarchy roleHierarchy) {
        this.roleHierarchy = roleHierarchy;
    }

    public void setAuthenticationTrustResolver(AuthenticationTrustResolver authenticationTrustResolver) {
        this.authenticationTrustResolver = authenticationTrustResolver;
    }

    private PermissionEvaluator permissionEvaluator = new DenyAllPermissionEvaluator();
    private RoleHierarchy roleHierarchy = new NullRoleHierarchy();
    private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

}
