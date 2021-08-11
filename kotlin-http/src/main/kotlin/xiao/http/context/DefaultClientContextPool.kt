package xiao.http.context

import xiao.beans.context.Context

/**
 *
 * @author lix wang
 */
class DefaultClientContextPool : ClientContextPool(Key) {
    companion object Key : Context.Key<DefaultClientContextPool>
}