package com.jkmayne.showmeburp

import burp.api.montoya.MontoyaApi
import burp.api.montoya.http.handler.RequestToBeSentAction
import burp.api.montoya.proxy.http.InterceptedRequest
import burp.api.montoya.proxy.http.ProxyRequestHandler
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction
import burp.api.montoya.proxy.http.ProxyRequestToBeSentAction
import org.w3c.dom.DOMConfiguration
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.ls.DOMImplementationLS
import org.xml.sax.InputSource
import post.delay.services.CryptLib
import java.io.StringReader
import java.io.StringWriter
import java.lang.Boolean
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.String
import kotlin.Unit


class ShowMeProxyHandler(private val api: MontoyaApi): ProxyRequestHandler {
    override fun handleRequestReceived(p0: InterceptedRequest?): ProxyRequestReceivedAction {
        //proxy got request into burp headed to server
        //need to parse xml and decrypt so we can mess with stuff
        api.logging().logToOutput("handleRequestReceived Invoked")
        if (p0 != null) {
            if (p0.url().contains("OLTPROMobDMService.svc")) {
                val domProc = showMeDOMProcess()
                //api.logging().logToOutput("handleRequestReceived is calling processDom with false")
                var editedBody = domProc.processDom(api, p0.body().toString(),false) //true = encryption

                editedBody = editedBody.replace("&lt;","<")
                editedBody = editedBody.replace("&gt;", ">")
                editedBody = editedBody.replace("<?xml version=\"1.0\"?>", "")

                //need to check for nested encrypted ssn
                //api.logging().logToOutput("handleProxyRequestReceived calling processSSN")
                editedBody = domProc.processSSN(api, editedBody, false)
                api.logging().logToOutput("proxy Request from client sending to burp: "+editedBody)
                return ProxyRequestReceivedAction.continueWith(p0.withBody(editedBody))
            }
        }
        return ProxyRequestReceivedAction.continueWith(p0)

    }


    override fun handleRequestToBeSent(p0: InterceptedRequest?): ProxyRequestToBeSentAction {
        //after we have some fun send this off to the server
        //we need to parse the xml and encrypt whatever needs encrypting
        //let the httphandler do all this...
        /*
        if (p0 != null) {
            if(p0.url().contains("OLTPROMobDMService.svc")) {
                // This is some old java shit
                val cLib = CryptLib()
                var body = p0.body().toString()
                val dbFactory = DocumentBuilderFactory.newInstance()
                val dBuilder = dbFactory.newDocumentBuilder()
                var doc: Document = dBuilder.parse(InputSource(StringReader(body)))
                doc.documentElement.normalize()
                //println("about to print root element...")
                api.logging().logToOutput("Root Element: " + doc.documentElement.nodeName)
                var nList: NodeList = doc.getElementsByTagName("strGetInfo")
                if(nList.length == 0) {
                    api.logging().logToOutput("Element not found")
                    return ProxyRequestToBeSentAction.continueWith(p0)
                } else {
                    val ls = doc.implementation as DOMImplementationLS
                    val ser = ls.createLSSerializer()
                    val config: DOMConfiguration = ser.getDomConfig()
                    config.setParameter("xml-declaration", Boolean.FALSE)

                    api.logging().logToOutput(nList.length.toString())
                    api.logging().logToOutput("Element FOUND!")
                    var reqOrig: String = ""
                    nList.forEach {
                        //api.logging()
                        //api.logging().logToOutput("Serializing: " + ser.writeToString(it))
                            //.logToOutput("Node Name: " + it.nodeName.toString() + " Node Value: " + it.nodeValue.toString())
                        //api.logging().logToOutput("Children nodes...")
                        it.childNodes.forEach { child ->
                            //for each child get values
                            //api.logging().logToOutput("Child Node Name: " + child.nodeName + " Value: " + child.nodeValue)
                            //this is the ciphertext...
                            //api.logging().logToOutput("Adding: " + child.textContent)
                            reqOrig += ser.writeToString(child)
                            //api.logging().logToOutput("Request being sent to server. Encrypting plaintext: " + child.textContent)
                            // val cleartext = cLib.encrypt(child.textContent)
                            //api.logging().logToOutput("Request Ciphertext: " + cleartext)
                            //child.textContent = cleartext //replace ciphertext with plaintext
                        }
                        //got all children values into string now to encrypt it
                        api.logging().logToOutput("Orig value: " + reqOrig)
                        val cipherText = cLib.encrypt(reqOrig)
                        api.logging().logToOutput("CipherText Variabel: " + cipherText)
                        it.textContent = cipherText
                        doc.importNode(it, true) //put the new nodes into the doc
                    }
                }


                val transformerFactory = TransformerFactory.newInstance()
                val transformer = transformerFactory.newTransformer()
                val source = DOMSource(doc)

                val outWriter = StringWriter()
                val result = StreamResult(outWriter)
                transformer.transform(source, result)
                val sb: StringBuffer = outWriter.getBuffer()
                val finalstring = sb.toString()
                api.logging().logToOutput(finalstring)
                return ProxyRequestToBeSentAction.continueWith(p0.withBody(finalstring))
            }
        }

         */

        return ProxyRequestToBeSentAction.continueWith(p0)
    }

    fun NodeList.forEach(action: (Node) -> Unit) {
        (0 until this.length)
            .asSequence()
            .map { this.item(it)}
            .forEach { action(it)}
    }
}