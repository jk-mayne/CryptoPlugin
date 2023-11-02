package com.jkmayne.showmeburp

import burp.api.montoya.BurpExtension
import burp.api.montoya.MontoyaApi

class ShowMeEntry: BurpExtension {
    /**
     * The entry point for the BurpCage extension.
     *
     * @param api An instance of the MontoyaApi
     */
    override fun initialize(api: MontoyaApi?) {
        /* Null safety check. PortSwigger didn't add the sufficient
         * annotations to its MontoyaApi interface, so Kotlin thinks
         * that it is possible for this object to be null. This is
         * just to make Kotlin happy.
         */
        if (api == null) {
            return
        }

        api.extension().setName("ShowMeBurp")
        api.http().registerHttpHandler(ShowMeHttpHandler(api))
        api.proxy().registerRequestHandler(ShowMeProxyHandler(api))
        api.proxy().registerResponseHandler(ShowMeProxyResponseHandler(api))
        //api.proxy().registerResponseHandler(ShowMeProxyHandler(api))
    }
}