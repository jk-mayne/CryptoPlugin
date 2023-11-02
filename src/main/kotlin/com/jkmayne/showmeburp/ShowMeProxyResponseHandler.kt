package com.jkmayne.showmeburp

import burp.api.montoya.MontoyaApi
import burp.api.montoya.proxy.http.InterceptedResponse
import burp.api.montoya.proxy.http.ProxyResponseHandler
import burp.api.montoya.proxy.http.ProxyResponseReceivedAction
import burp.api.montoya.proxy.http.ProxyResponseToBeSentAction
import org.w3c.dom.Node
import org.w3c.dom.NodeList

//class ShowMeProxyHandler(private val api: MontoyaApi): ProxyRequestHandler
class ShowMeProxyResponseHandler(private val api: MontoyaApi): ProxyResponseHandler {
    override fun handleResponseReceived(p0: InterceptedResponse?): ProxyResponseReceivedAction {
        //This function re-encrypts the payload before sending back to the client device

        if (p0 != null) {
            if(p0.contains("mytaxoffice-com", true)) //custom header that they send
            {
                val domProc = showMeDOMProcess()
                var editedBody: String
                editedBody = domProc.processSSN(api,p0.body().toString(), true)
                editedBody = domProc.processDom(api, editedBody,true) //true = encryption

                editedBody = editedBody.replace("&lt;","<")
                editedBody = editedBody.replace("&gt;", ">")
                editedBody = editedBody.replace("<?xml version=\"1.0\"?>", "")
                api.logging().logToOutput("proxyresponse sending to client: " + editedBody)
                return ProxyResponseReceivedAction.continueWith(p0.withBody(editedBody))
            }
        }
        return ProxyResponseReceivedAction.continueWith(p0)
    }

    override fun handleResponseToBeSent(p0: InterceptedResponse?): ProxyResponseToBeSentAction {
        //this function exists before data is displayed within burp's proxy...
        return ProxyResponseToBeSentAction.continueWith(p0)
    }

    //function for handling nodelist iteration
    fun NodeList.forEach(action: (Node) -> Unit) {
        (0 until this.length)
            .asSequence()
            .map { this.item(it)}
            .forEach { action(it)}
    }
}