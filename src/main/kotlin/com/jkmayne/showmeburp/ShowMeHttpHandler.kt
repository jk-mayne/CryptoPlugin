package com.jkmayne.showmeburp

import burp.api.montoya.MontoyaApi
import burp.api.montoya.http.handler.*
import burp.api.montoya.http.message.HttpHeader
import burp.api.montoya.http.message.requests.HttpRequest
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.ls.DOMImplementationLS
import org.xml.sax.InputSource
import post.delay.services.CryptLib
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class ShowMeHttpHandler(private val api: MontoyaApi) : HttpHandler {

    override fun handleHttpRequestToBeSent(p0: HttpRequestToBeSent?): RequestToBeSentAction {
        //api.logging().logToOutput("made it into the handler")
        //before we send stuff off we need to re-encrypt it
       var body = p0?.body().toString()
       if (p0 != null) {
            if(p0.url().contains("service endpoint REPLACE ME")) {

                val domProc = showMeDOMProcess()

                api.logging().logToOutput("handleHttpRequestToBeSent calling processSSN")
                body = domProc.processSSN(api,p0.body().toString(),true) //encrypt ssn param if present

                body = domProc.processDom(api, body,true) //true = encryption

                body = body.replace("&lt;","<")
                body = body.replace("&gt;", ">")
                body = body.replace("<?xml version=\"1.0\"?>", "")
                //api.logging().logToOutput("EDITED BODY: " + editedBody)
                api.logging().logToOutput("HTTP Request sendig to server: "+body)
                return RequestToBeSentAction.continueWith(p0.withBody(body))
            }
        }
        return RequestToBeSentAction.continueWith(p0)
    }

    fun NodeList.forEach(action: (Node) -> Unit) {
        (0 until this.length)
            .asSequence()
            .map { this.item(it)}
            .forEach { action(it)}
    }

    override fun handleHttpResponseReceived(p0: HttpResponseReceived?): ResponseReceivedAction {

        if (p0 != null) {
            if(p0.hasHeader("X-Powered-By","mytaxoffice-com")) {
                //api.logging().logToOutput("I see Envelope")
                val domProc = showMeDOMProcess()
                //api.logging().logToOutput("ResponseRecived is calling processDom with false")
                var editedBody = domProc.processDom(api, p0.body().toString(),false) //false = decryption

                editedBody = editedBody.replace("&lt;","<")
                editedBody = editedBody.replace("&gt;", ">")
                editedBody = editedBody.replace("<?xml version=\"1.0\"?>", "")
                //api.logging().logToOutput(editedBody)
                //api.logging().logToOutput("handleHttpResponseReceived calling processSSN")
                editedBody = domProc.processSSN(api, editedBody, false) //handle ssn tags if present
                api.logging().logToOutput("HTTP Response recieved sending to burp: "+editedBody)
                return ResponseReceivedAction.continueWith(p0.withBody(editedBody))
            }
            //api.logging().logToOutput("mytaxoffice-com header not found")
        }

        //api.logging().logToOutput("ResponseRecieved did not find valid xml, no edits made")
        //api.logging().logToOutput(p0.toString())
        return ResponseReceivedAction.continueWith(p0)
    }

}