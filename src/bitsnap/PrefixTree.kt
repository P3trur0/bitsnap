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

package bitsnap

import java.util.*

internal data class PrefixTree<T>(val rootNode: Node<T>) : Collection<T> {

    open class Node<T>(
        val prefix: CharSequence,
        val children: Array<Node<T>>
    ) {
        override fun equals(other: Any?): Boolean = if (other is Node<*>) {
            prefix == other.prefix &&
                children.indices.firstOrNull { children[it] != other.children[it] } == null
        } else {
            false
        }

        override fun hashCode(): Int =
            prefix.hashCode() +
                children.map { node: Node<*> -> node.hashCode() }
                    .reduce { hash, elem -> hash + elem }
    }

    class LeafNode<T>(
        prefix: CharSequence,
        children: Array<Node<T>>,
        val value: T
    ) : Node<T>(prefix, children) {

        override fun equals(other: Any?) = if (other is LeafNode<*>) {
            super.equals(other) && value == other.value
        } else {
            false
        }

        override fun hashCode(): Int = super.hashCode() + this.value!!.hashCode()
    }

    val values: List<T> by lazy {
        val nodes: MutableSet<Node<T>> = HashSet()
        val nodesToSearch: MutableSet<Node<T>> = HashSet()

        nodesToSearch.add(rootNode)

        do {
            nodesToSearch.forEach {
                nodes.add(it)
                nodesToSearch.addAll(it.children)
            }

            nodesToSearch.removeAll(nodes)

        } while (nodesToSearch.size > 0)

        nodes.filter { it is LeafNode }
            .map { (it as LeafNode).value }
            .toList()
    }

    override val size by lazy {
        values.size
    }

    fun search(word: CharSequence): T? {

        var searchNodes: Array<Node<T>> = rootNode.children
        var searchWord = word

        if (!searchWord.startsWith(rootNode.prefix)) {
            return null
        }

        if (searchWord == rootNode.prefix && rootNode is LeafNode) {
            return rootNode.value
        }

        do {
            var target = searchNodes.firstOrNull {
                searchWord.length >= it.prefix.length && searchWord.startsWith(it.prefix)
            }

            if (target != null) {
                if (target.prefix == searchWord && target is LeafNode) {
                    return target.value
                }

                searchWord.removePrefix(target.prefix)
                searchNodes = target.children
            }

        } while (target != null)

        return null
    }

    override fun contains(element: T) = values.contains(element)

    override fun containsAll(elements: Collection<T>) = elements.firstOrNull { !contains(it) } == null

    override fun iterator() = values.iterator()

    override fun isEmpty() = rootNode.children.isEmpty()

    operator fun get(chars: CharSequence): T? = search(chars)

    companion object {
        operator fun <T> invoke(map: Map<CharSequence, T>) {
            map.keys.sortedWith(Comparator { one, other ->
                0
            })
        }
    }
}
