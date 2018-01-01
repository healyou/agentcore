package com.mycompany

/**
 * @author Nikita Gorodilov
 */
class HasAllAuthoritiesPrincipalAcceptor : PrincipalAcceptor {

    //    private Authority[] authorities;
    //
    //    public HasAllAuthoritiesPrincipalAcceptor(Authority... authorities) {
    //        this.authorities = authorities;
    //    }

    override fun accept(principal: Principal): Boolean {
        return true
        //return Arrays.stream(authorities).allMatch(principal.getAuthorities()::contains);
    }
}
