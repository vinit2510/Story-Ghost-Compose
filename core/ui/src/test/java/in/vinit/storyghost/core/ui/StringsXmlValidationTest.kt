package `in`.vinit.storyghost.core.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import java.util.Locale
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory

class StringsXmlValidationTest {

    private val formatRegex = Pattern.compile("%(\\d+\\$)?[a-zA-Z]")

    @Test
    fun testAllStringsXmlAreValidAndDoNotCrashAtRuntime() {
        val cwd = File(".").canonicalFile
        val resDir = if (File(cwd, "src/main/res").exists()) {
            File(cwd, "src/main/res")
        } else {
            File(cwd, "core/ui/src/main/res")
        }
        assertTrue("Resource directory must exist: ${resDir.absolutePath}", resDir.exists())

        val valueDirs = resDir.listFiles { file ->
            file.isDirectory && file.name.startsWith("values")
        }?.sortedBy { it.name } ?: emptyList()

        assertTrue("Should have multiple values directories", valueDirs.size >= 7)

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()

        // 1. Parse default strings.xml as reference
        val defaultFile = File(resDir, "values/strings.xml")
        assertTrue("Default strings.xml must exist", defaultFile.exists())
        val defaultDoc = builder.parse(defaultFile)
        val defaultStrings = extractStrings(defaultDoc.documentElement)
        val defaultPlurals = extractPlurals(defaultDoc.documentElement)

        // Map of known format arguments for testing String.format
        val testArgsMap = mapOf(
            "mention_count" to arrayOf(1, 5),
            "mention_total_count" to arrayOf(10),
            "highlights" to arrayOf("vinit"),
            "plan_activated" to arrayOf("Monthly"),
            "effective_price_per_month" to arrayOf("$1.99"),
            "price_per_month" to arrayOf("$4.99"),
            "price_per_year" to arrayOf("$19.99")
        )

        // 2. Validate every language
        for (dir in valueDirs) {
            val stringsFile = File(dir, "strings.xml")
            if (!stringsFile.exists()) continue

            val langCode = if (dir.name == "values") "en" else dir.name.removePrefix("values-").replace("-r", "-")
            val locale = Locale.forLanguageTag(langCode)
            val doc = builder.parse(stringsFile)
            val currentStrings = extractStrings(doc.documentElement)
            val currentPlurals = extractPlurals(doc.documentElement)

            // A. Validate each string
            for ((key, text) in currentStrings) {
                val defaultText = defaultStrings[key]
                if (defaultText != null) {
                    val defaultSpecs = extractFormatSpecifiers(defaultText)
                    val currentSpecs = extractFormatSpecifiers(text)

                    assertEquals(
                        "Format specifiers in $key for locale $langCode must match default. Expected $defaultSpecs, found $currentSpecs in '$text'",
                        defaultSpecs,
                        currentSpecs
                    )
                }

                // If this string expects format arguments, test String.format() with real args
                val testArgs = testArgsMap[key]
                if (testArgs != null) {
                    try {
                        val formatted = String.format(locale, text, *testArgs)
                        assertNotNull("Formatted result for $key ($langCode) should not be null", formatted)
                        assertTrue("Formatted result should not be blank", formatted.isNotBlank())
                    } catch (e: Exception) {
                        throw AssertionError("String.format failed for '$key' in locale [$langCode] with text: '$text'", e)
                    }
                }
            }

            // B. Validate each plural
            for ((key, items) in currentPlurals) {
                for ((quantity, text) in items) {
                    try {
                        val formatted = String.format(locale, text, 5)
                        assertNotNull(formatted)
                    } catch (e: Exception) {
                        throw AssertionError("Plural formatting failed for '$key' ($quantity) in locale [$langCode] with text: '$text'", e)
                    }
                }
            }
        }
    }

    private fun extractStrings(root: Element): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val nodeList = root.getElementsByTagName("string")
        for (i in 0 until nodeList.length) {
            val element = nodeList.item(i) as Element
            val name = element.getAttribute("name")
            val text = element.textContent ?: ""
            map[name] = text
        }
        return map
    }

    private fun extractPlurals(root: Element): Map<String, Map<String, String>> {
        val map = mutableMapOf<String, Map<String, String>>()
        val nodeList = root.getElementsByTagName("plurals")
        for (i in 0 until nodeList.length) {
            val element = nodeList.item(i) as Element
            val name = element.getAttribute("name")
            val items = mutableMapOf<String, String>()
            val itemNodes = element.getElementsByTagName("item")
            for (j in 0 until itemNodes.length) {
                val itemElement = itemNodes.item(j) as Element
                val quantity = itemElement.getAttribute("quantity")
                items[quantity] = itemElement.textContent ?: ""
            }
            map[name] = items
        }
        return map
    }

    private fun extractFormatSpecifiers(text: String): List<String> {
        val list = mutableListOf<String>()
        val matcher = formatRegex.matcher(text)
        while (matcher.find()) {
            list.add(matcher.group())
        }
        return list
    }
}
