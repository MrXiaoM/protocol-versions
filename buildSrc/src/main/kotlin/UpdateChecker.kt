import org.gradle.api.Project
import org.gradle.internal.impldep.com.google.gson.JsonParser
import java.io.File
import java.net.URL
import java.net.URLConnection


fun URLConnection.applyHeaders() {
    setRequestProperty("Accept", "application/json, text/plain, */*")
    setRequestProperty("Origin", "https://im.qq.com")
    setRequestProperty("Priority", "u=1, i")
    setRequestProperty("Referer", "https://im.qq.com/")
    setRequestProperty("Sec-Fetch-Dest", "empty")
    setRequestProperty("Sec-Fetch-Mode", "cors")
    setRequestProperty("Sec-Fetch-Site", "cross-site")
    setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Mobile Safari/537.36 Edg/129.0.0.0")
}
fun get(url: String): String {
    val conn = URL(url).openConnection()
    conn.connectTimeout = 30 * 1000
    conn.readTimeout = 30 * 1000
    conn.applyHeaders()
    return conn.getInputStream().use { it.readBytes().toString(Charsets.UTF_8) }
}

fun Project.checkUpdate() {
    val mobileConfigStr = get("https://cdn-go.cn/qq-web/im.qq.com_new/latest/rainbow/mobileConfig.json")
    println("已取得更新配置 $mobileConfigStr")
    val mobileConfig = JsonParser.parseString(mobileConfigStr).asJsonObject
    val android = mobileConfig["android"]?.asJsonObject ?: throw IllegalStateException("找不到 android")
    val version = android["version"]?.asString ?: throw IllegalStateException("找不到 android.version")
    val apkLink = android["x64Link"]?.asString ?: throw IllegalStateException("找不到 android.x64Link")
    println()
    println(apkLink)
    println(version)
    if (File(rootDir, "master/android_pad/$version.json").exists()) {
        println("仓库中已有该版本，无需更新")
        return
    }
    File(rootDir, "download_apk.sh").writeText("wget --progress=dot:mega -O eden/Eden.apk $apkLink")
    File(rootDir, "apk_version").writeText(version)
}
