/**
 *  Copyright 2016 Yuriy Yarosh
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 **/

package bitsnap.reflect

import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLClassLoader
import java.util.*
import java.util.jar.Attributes
import java.util.jar.JarFile

internal object Scanner {

    internal fun scanJar(file: File, loader: ClassLoader): List<String> = try {
        JarFile(file).use { jarFile ->
            val classpath = jarFile.manifest?.mainAttributes?.getValue(Attributes.Name.CLASS_PATH)

            return if (classpath != null) {
                val files = classpath.split(" ")
                    .filter { it.isNotEmpty() }
                    .filter {
                        try {
                            val url = URL(file.toURI().toURL(), it)
                            url.protocol == "file"
                        } catch (e: MalformedURLException) {
                            false
                        }
                    }

                val entries: List<String> = jarFile.entries().asSequence().filter {
                    !it.isDirectory && it.name != JarFile.MANIFEST_NAME
                }.map { it.name }.toList()

                val resources: MutableList<String> = ArrayList(files.size + entries.size)
                resources.addAll(entries.filter { !it.contains("$") }.map {
                    it.removeSuffix(".class").replace('/', '.')
                })
                resources.addAll(files.flatMap { scanJar(File(it), loader) })

                resources
            } else {
                emptyList()
            }
        }
    } catch (e: IOException) {
        emptyList()
    }

    internal fun scan(file: File, loader: ClassLoader) = if (file.isDirectory) {
        val resourceFiles: MutableList<String> = LinkedList()
        var resourceDirectories: MutableList<File> = LinkedList()

        resourceDirectories.add(file)

        while (resourceDirectories.isNotEmpty()) {
            val newDirectories = resourceDirectories.flatMap { dir ->
                (dir.listFiles() ?: emptyArray()).asIterable().map {

                    if (it.isDirectory) {
                        it
                    } else {
                        val path = it.absolutePath
                        if (path.endsWith(".class") && !path.contains('$')) {
                            resourceFiles.add(path.removePrefix("${file.absolutePath}/").removeSuffix(".class").replace('/', '.'))
                        }

                        null
                    }
                }.filterNotNull()
            }

            resourceDirectories.clear()
            resourceDirectories.addAll(newDirectories)
        }

        resourceFiles
    } else {
        scanJar(file, loader)
    }

    fun resources(loader: ClassLoader): List<ClassPath.ClassResource> {

        val loaders: MutableList<URLClassLoader> = LinkedList()

        var l = loader
        do {
            if (l is URLClassLoader) {
                loaders.add(l)
            }

            l = loader.parent
        } while (l.parent != null)

        if (l is URLClassLoader) {
            loaders.add(l)
        }

        return loaders.flatMap { l ->
            l.urLs.asIterable().asSequence()
                .filter { it.protocol == "file" }
                .map { File(it.file) }
                .filter { it.exists() }
                .toList()
                .flatMap { scan(it, l) }
                .toSet()
                .asSequence()
                .map {
                    ClassPath.ClassResource(it)
                }.filterNotNull().toList()
        }
    }
}
