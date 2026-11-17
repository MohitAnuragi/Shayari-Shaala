package com.shayarishaala.shayarishaala

import com.google.ai.client.generativeai.type.UnknownException
import com.shayarishaala.shayarishaala.ui.kalam.kalamErrorMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException

class KalamErrorMessageTest {

    private val offline = "इंटरनेट कनेक्शन जाँचें और दोबारा कोशिश करें"

    @Test
    fun offlineErrors_askUserToCheckInternet() {
        assertEquals(offline, kalamErrorMessage(UnknownHostException("generativelanguage.googleapis.com")))
        assertEquals(offline, kalamErrorMessage(UnresolvedAddressException()))
    }

    @Test
    fun sdkWrappedOfflineError_isRecognisedFromItsCause() {
        val wrapped = UnknownException("Something unexpected happened.", UnknownHostException("host"))
        assertEquals(offline, kalamErrorMessage(wrapped))
    }

    @Test
    fun timeout_isNotReportedAsOffline() {
        assertEquals("समय ज़्यादा लग गया, दोबारा कोशिश करें", kalamErrorMessage(SocketTimeoutException()))
    }

    @Test
    fun unknownErrors_neverExposeTheRawMessage() {
        val message = kalamErrorMessage(RuntimeException("API key AIza-secret quota details"))
        assertFalse(message.contains("AIza"))
        assertFalse(message.contains("API"))
    }
}
