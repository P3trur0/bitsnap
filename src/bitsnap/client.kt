package bitsnap

import bitsnap.http.Method
import bitsnap.http.Request
import bitsnap.http.Response
import java.net.URL

abstract class Client(val timeout: Int) {

    abstract fun perform(request: Request, then: (Response) -> Unit)

    fun Options(url: URL, init: Request.Builder.() -> Unit = {}, then: (Response) -> Unit) =
         perform(Request.Builder().invoke(Method.OPTIONS, url, init), then)

    fun Get(url: URL, init: Request.Builder.() -> Unit = {}, then: (Response) -> Unit) =
        perform(Request.Builder().invoke(Method.GET, url, init), then)

    fun Head(url: URL, init: Request.Builder.() -> Unit = {}, then: (Response) -> Unit) =
        perform(Request.Builder().invoke(Method.HEAD, url, init), then)

    fun Put(url: URL, init: Request.Builder.() -> Unit = {}, then: (Response) -> Unit) =
        perform(Request.Builder().invoke(Method.PUT, url, init), then)

    fun Post(url: URL, init: Request.Builder.() -> Unit = {}, then: (Response) -> Unit) =
        perform(Request.Builder().invoke(Method.POST, url, init), then)

    fun Delete(url: URL, init: Request.Builder.() -> Unit = {}, then: (Response) -> Unit) =
        perform(Request.Builder().invoke(Method.PUT, url, init), then)

    fun Trace(url: URL, init: Request.Builder.() -> Unit = {}, then: (Response) -> Unit) =
        perform(Request.Builder().invoke(Method.TRACE, url, init), then)

    fun Connect(url: URL, init: Request.Builder.() -> Unit = {}, then: (Response) -> Unit) =
        perform(Request.Builder().invoke(Method.CONNECT, url, init), then)
}

class NioClient(val timeout: Int) {

}
