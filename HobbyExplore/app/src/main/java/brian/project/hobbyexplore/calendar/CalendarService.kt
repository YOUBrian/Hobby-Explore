//package brian.project.hobbyexplore.calendar
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
//import com.google.api.client.json.JsonFactory
//import com.google.api.client.json.JsonFactoryInitializer
//import com.google.api.client.json.JsonFactoryInitializer.getInstance
//import com.google.api.services.calendar.Calendar
//import com.google.api.services.calendar.CalendarScopes
//import com.google.api.services.calendar.CalendarScopes.CALENDAR
//import com.google.api.services.calendar.model.Event
//
//import java.io.IOException
//import java.security.GeneralSecurityException
//import java.util.*
//import kotlin.collections.ArrayList
//import kotlin.collections.List
//import android.Manifest.permission_group.CALENDAR
//
//
//import com.google.api.client.auth.oauth2.Credential
//import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
//import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
//import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
//import com.google.api.client.http.javanet.NetHttpTransport
//import com.google.api.client.util.store.FileDataStoreFactory
//import java.io.File
//import java.io.InputStreamReader
//
//import java.util.Collections
//
//private val APPLICATION_NAME = "Your App Name"
//private val JSON_FACTORY: JsonFactory = getInstance()
//private val CREDENTIALS_FILE_PATH = "/path/to/your/credentials.json" // 請替換成你的認證文件的路徑
//
//private fun getCalendarService(): Calendar {
//    val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
//    val credential = getCredentials(httpTransport)
//    return Calendar.Builder(httpTransport, JSON_FACTORY, credential)
//        .setApplicationName(APPLICATION_NAME)
//        .build()
//}
//
//private fun getCredentials(httpTransport: NetHttpTransport): Credential {
//    // 讀取認證文件
//    val inputStream = CalendarSample::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
//    val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))
//
//    // 設定用戶的權限範圍
//    val scopes = Collections.singletonList(CALENDAR)
//
//    // 創建認證流程並返回憑證
//    val flow = GoogleAuthorizationCodeFlow.Builder(
//        httpTransport, JSON_FACTORY, clientSecrets, scopes
//    )
//        .setDataStoreFactory(FileDataStoreFactory(File("tokens")))
//        .setAccessType("offline")
//        .build()
//    val receiver = LocalServerReceiver.Builder().setPort(8888).build()
//    return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
//}