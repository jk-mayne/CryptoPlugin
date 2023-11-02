package com.jkmayne.showmeburp

import burp.api.montoya.MontoyaApi
import burp.api.montoya.proxy.http.ProxyRequestReceivedAction
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

class showMeDOMProcess {

    public fun processSSN(api: MontoyaApi, body: String, operation: Boolean): String {
        //handle ssn tag within xml string
        //api.logging().logToOutput("processSSN called with: " + body)
        //api.logging().logToOutput("Operation: " + operation.toString())
        if(body.contains("ssn")) {
            api.logging().logToOutput("found ssn tag")
            //editedBody = domProc.processDom(api, editedBody, false)
            var sub = body.substringAfter("<ssn>").substringBefore("</ssn>")
            api.logging().logToOutput("Sub is: "+sub)
            val cLib = CryptLib()
            if(operation) {
                sub = cLib.encrypt(sub)
                api.logging().logToOutput("encryption result "+sub)
            } else {
                sub = cLib.decrypt(sub)
                api.logging().logToOutput("decryption result " +sub)
            }

            val regex = "</?ssn.*?>*</?ssn.?>".toRegex() // matches with every <tag> or </tag>
            //api.logging().logToOutput("Sub is: $sub")
            var body2 = body.replace(regex, "<ssn>$sub</ssn>")
            //api.logging().logToOutput("replaced ssn body: " + body2)
            return body2
            //api.logging().logToOutput("Decrypted ssn tag body: " + editedBody)
        }
        api.logging().logToOutput("You called processSSN but there is no ssn tag.")
        return body
    }

    public fun processDom(api: MontoyaApi, body: String, operation: Boolean): String {
        //take in the dom body as a string and process the xml with encryption or decryption

        val cLib = CryptLib()
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()

        var doc: Document = dBuilder.parse(InputSource(StringReader(body)))
        doc.documentElement.normalize()

        val root = doc.documentElement

        var myNode: Node = root.firstChild
        var encTag: String = ""

        while (myNode.nextSibling != null || myNode.hasChildNodes()) { //if the node has siblings or children we need to process
            //api.logging().logToOutput("Checking tag: " + myNode.nodeName)
            when (myNode.nodeName) {
                "strGetInfo" -> encTag = "strGetInfo"
                "strPreparerInfo" -> encTag = "strPreparerInfo"
                "strExistClientInfo" -> encTag = "strExistClientInfo"
                "GetReturnInfoMobResult" -> encTag = "GetReturnInfoMobResult"
                "GetPreparerContactInfoMobResult" -> encTag = "GetPreparerContactInfoMobResult"
                "strCustomerInfo" -> encTag = "strCustomerInfo"
                "strReturnStatusInfo" -> encTag = "strReturnStatusInfo"
                "GetReturnStatusMobResult" -> encTag = "GetReturnStatusMobResult"
                "ExistingclientInfo" -> encTag = "ExistingclientInfo"
                "strAccInfo" -> encTag = "strAccInfo"
                "GetLoginInfoMobResult" -> encTag = "GetLoginInfoMobResult"
                "strAPPUpdateStatus" -> encTag = "strAPPUpdateStatus"
                "strUserManageInfo" -> encTag = "strUserManageInfo"
                "GetExistCustomerInfoMobResult" -> encTag = "GetExistCustomerInfoMobResult"
                "GetScanManagementInfoMobResult" -> encTag = "GetScanManagementInfoMobResult"
                "GetImageViewPathMobResult" -> encTag = "GetImageViewPathMobResult"
                "strClientInfo" -> encTag = "strClientInfo"
                "GetMessageCountAndInfoMobResult" -> encTag = "GetMessageCountAndInfoMobResult"
                "GetMessageDetailsMobResult" -> encTag = "GetMessageDetailsMobResult"
                "GetDocumentSummaryMobResult" -> encTag = "GetDocumentSummaryMobResult"
                "strUploadFileInfo" -> encTag = "strUploadFileInfo"
                "UploadDocumentFileMobNewResult" -> encTag = "UploadDocumentFileMobNewResult"
                "strHistoryInfo" -> encTag = "strHistoryInfo"
                "UploadHistoryResult" -> encTag = "UploadHistoryResult"
                "GetImageViewPathMobNewResult" -> encTag = "GetImageViewPathMobNewResult"
                "strUserInfo" -> encTag = "strUserInfo"
                "ForgotPasswordInfoMobResult" -> encTag = "ForgotPasswordInfoMobResult"
                "BizCheckIfUserAlreadyExistMobResult" -> encTag = "BizCheckIfUserAlreadyExistMobResult"
                else -> {
                    //api.logging().logToOutput("Tag doesn't match moving on")
                }
            }
            if (encTag == "") {
                if (myNode.hasChildNodes()) {
                    myNode = myNode.firstChild
                } else {
                    myNode = myNode.nextSibling
                }
            } else {
                //api.logging().logToOutput("Found a tag!")
                break
            }
            //looping through the all the nodes
            //if node has children process those
        }
        //api.logging().logToOutput("No other siblings")
        //api.logging().logToOutput("ENC TAG: " + encTag)
        if (encTag == "") {
            api.logging().logToOutput("No tag matched sending request on")
            return body //send unedited body back
        }

        var nList: NodeList = doc.getElementsByTagName(encTag)

        val ls = doc.implementation as DOMImplementationLS
        val ser = ls.createLSSerializer()
        ser.domConfig.setParameter("xml-declaration", false) //attempts to avoid xml decs but idk if works


        var reqOrig: String = ""
        nList.forEach {
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


            var cipherText = ""
            cipherText = if (operation) { //true for enc
                //api.logging().logToOutput("Encrypting: "+ reqOrig)
                cLib.encrypt(reqOrig)
            } else {
                //api.logging().logToOutput("Decrypting: " + reqOrig)
                cLib.decrypt(reqOrig)
                //api.logging().logToOutput("just decrypted: " + reqOrig)
            }
            //api.logging().logToOutput("Just did our operation: "+reqOrig)
            it.textContent = cipherText
            doc.importNode(it, true) //put the new nodes into the doc
        }

        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
        val source = DOMSource(doc)

        val outWriter = StringWriter()
        val result = StreamResult(outWriter)
        transformer.transform(source, result)
        val sb: StringBuffer = outWriter.getBuffer()
        var finalstring = sb.toString()
        return finalstring
    }


    fun NodeList.forEach(action: (Node) -> Unit) {
        (0 until this.length)
            .asSequence()
            .map { this.item(it) }
            .forEach { action(it) }
    }
}

