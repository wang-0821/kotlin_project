package xiao.redis.client.testing

import io.lettuce.core.BitFieldArgs
import io.lettuce.core.Consumer
import io.lettuce.core.GeoArgs
import io.lettuce.core.GeoCoordinates
import io.lettuce.core.GeoRadiusStoreArgs
import io.lettuce.core.GeoWithin
import io.lettuce.core.KeyScanCursor
import io.lettuce.core.KeyValue
import io.lettuce.core.KillArgs
import io.lettuce.core.LPosArgs
import io.lettuce.core.Limit
import io.lettuce.core.MapScanCursor
import io.lettuce.core.MigrateArgs
import io.lettuce.core.Range
import io.lettuce.core.RedisFuture
import io.lettuce.core.RestoreArgs
import io.lettuce.core.ScanArgs
import io.lettuce.core.ScanCursor
import io.lettuce.core.ScoredValue
import io.lettuce.core.ScoredValueScanCursor
import io.lettuce.core.ScriptOutputType
import io.lettuce.core.SetArgs
import io.lettuce.core.SortArgs
import io.lettuce.core.StrAlgoArgs
import io.lettuce.core.StreamMessage
import io.lettuce.core.StreamScanCursor
import io.lettuce.core.StringMatchResult
import io.lettuce.core.TrackingArgs
import io.lettuce.core.TransactionResult
import io.lettuce.core.UnblockType
import io.lettuce.core.Value
import io.lettuce.core.ValueScanCursor
import io.lettuce.core.XAddArgs
import io.lettuce.core.XClaimArgs
import io.lettuce.core.XGroupCreateArgs
import io.lettuce.core.XReadArgs
import io.lettuce.core.ZAddArgs
import io.lettuce.core.ZStoreArgs
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.models.stream.PendingMessage
import io.lettuce.core.models.stream.PendingMessages
import io.lettuce.core.output.CommandOutput
import io.lettuce.core.output.KeyStreamingChannel
import io.lettuce.core.output.KeyValueStreamingChannel
import io.lettuce.core.output.ScoredValueStreamingChannel
import io.lettuce.core.output.ValueStreamingChannel
import io.lettuce.core.protocol.CommandArgs
import io.lettuce.core.protocol.CommandType
import io.lettuce.core.protocol.ProtocolKeyword
import xiao.redis.client.service.RedisAsyncService
import java.time.Duration
import java.util.Date

/**
 *
 * @author lix wang
 */
abstract class AbstractTestingRedisAsyncService : RedisAsyncService {
    override fun publish(channel: String?, message: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun pubsubChannels(): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun pubsubChannels(channel: String?): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun pubsubNumsub(vararg channels: String?): RedisFuture<MutableMap<String, Long>> {
        throw UnsupportedOperationException()
    }

    override fun pubsubNumpat(): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun echo(msg: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun role(): RedisFuture<MutableList<Any>> {
        throw UnsupportedOperationException()
    }

    override fun ping(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun readOnly(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun readWrite(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun quit(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun waitForReplication(replicas: Int, timeout: Long): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> dispatch(
        type: ProtocolKeyword?,
        output: CommandOutput<String, String, T>?
    ): RedisFuture<T> {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> dispatch(
        type: ProtocolKeyword?,
        output: CommandOutput<String, String, T>?,
        args: CommandArgs<String, String>?
    ): RedisFuture<T> {
        throw UnsupportedOperationException()
    }

    override fun isOpen(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun reset() {
        throw UnsupportedOperationException()
    }

    override fun setAutoFlushCommands(autoFlush: Boolean) {
        throw UnsupportedOperationException()
    }

    override fun flushCommands() {
        throw UnsupportedOperationException()
    }

    override fun geoadd(key: String?, longitude: Double, latitude: Double, member: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun geoadd(key: String?, vararg lngLatMember: Any?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun geohash(key: String?, vararg members: String?): RedisFuture<MutableList<Value<String>>> {
        throw UnsupportedOperationException()
    }

    override fun georadius(
        key: String?,
        longitude: Double,
        latitude: Double,
        distance: Double,
        unit: GeoArgs.Unit?
    ): RedisFuture<MutableSet<String>> {
        throw UnsupportedOperationException()
    }

    override fun georadius(
        key: String?,
        longitude: Double,
        latitude: Double,
        distance: Double,
        unit: GeoArgs.Unit?,
        geoArgs: GeoArgs?
    ): RedisFuture<MutableList<GeoWithin<String>>> {
        throw UnsupportedOperationException()
    }

    override fun georadius(
        key: String?,
        longitude: Double,
        latitude: Double,
        distance: Double,
        unit: GeoArgs.Unit?,
        geoRadiusStoreArgs: GeoRadiusStoreArgs<String>?
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun georadiusbymember(
        key: String?,
        member: String?,
        distance: Double,
        unit: GeoArgs.Unit?
    ): RedisFuture<MutableSet<String>> {
        throw UnsupportedOperationException()
    }

    override fun georadiusbymember(
        key: String?,
        member: String?,
        distance: Double,
        unit: GeoArgs.Unit?,
        geoArgs: GeoArgs?
    ): RedisFuture<MutableList<GeoWithin<String>>> {
        throw UnsupportedOperationException()
    }

    override fun georadiusbymember(
        key: String?,
        member: String?,
        distance: Double,
        unit: GeoArgs.Unit?,
        geoRadiusStoreArgs: GeoRadiusStoreArgs<String>?
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun geopos(key: String?, vararg members: String?): RedisFuture<MutableList<GeoCoordinates>> {
        throw UnsupportedOperationException()
    }

    override fun geodist(key: String?, from: String?, to: String?, unit: GeoArgs.Unit?): RedisFuture<Double> {
        throw UnsupportedOperationException()
    }

    override fun hdel(key: String?, vararg fields: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun hexists(key: String?, field: String?): RedisFuture<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun hget(key: String?, field: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun hincrby(key: String?, field: String?, amount: Long): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun hincrbyfloat(key: String?, field: String?, amount: Double): RedisFuture<Double> {
        throw UnsupportedOperationException()
    }

    override fun hgetall(key: String?): RedisFuture<MutableMap<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun hgetall(channel: KeyValueStreamingChannel<String, String>?, key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun hkeys(key: String?): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun hkeys(channel: KeyStreamingChannel<String>?, key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun hlen(key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun hmget(key: String?, vararg fields: String?): RedisFuture<MutableList<KeyValue<String, String>>> {
        throw UnsupportedOperationException()
    }

    override fun hmget(
        channel: KeyValueStreamingChannel<String, String>?,
        key: String?,
        vararg fields: String?
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun hmset(key: String?, map: MutableMap<String, String>?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun hscan(key: String?): RedisFuture<MapScanCursor<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun hscan(key: String?, scanArgs: ScanArgs?): RedisFuture<MapScanCursor<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun hscan(
        key: String?,
        scanCursor: ScanCursor?,
        scanArgs: ScanArgs?
    ): RedisFuture<MapScanCursor<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun hscan(key: String?, scanCursor: ScanCursor?): RedisFuture<MapScanCursor<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun hscan(
        channel: KeyValueStreamingChannel<String, String>?,
        key: String?
    ): RedisFuture<StreamScanCursor> {
        throw UnsupportedOperationException()
    }

    override fun hscan(
        channel: KeyValueStreamingChannel<String, String>?,
        key: String?,
        scanArgs: ScanArgs?
    ): RedisFuture<StreamScanCursor> {
        throw UnsupportedOperationException()
    }

    override fun hscan(
        channel: KeyValueStreamingChannel<String, String>?,
        key: String?,
        scanCursor: ScanCursor?,
        scanArgs: ScanArgs?
    ): RedisFuture<StreamScanCursor> {
        throw UnsupportedOperationException()
    }

    override fun hscan(
        channel: KeyValueStreamingChannel<String, String>?,
        key: String?,
        scanCursor: ScanCursor?
    ): RedisFuture<StreamScanCursor> {
        throw UnsupportedOperationException()
    }

    override fun hset(key: String?, field: String?, value: String?): RedisFuture<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun hset(key: String?, map: MutableMap<String, String>?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun hsetnx(key: String?, field: String?, value: String?): RedisFuture<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun hstrlen(key: String?, field: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun hvals(key: String?): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun hvals(channel: ValueStreamingChannel<String>?, key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun pfadd(key: String?, vararg values: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun pfmerge(destkey: String?, vararg sourcekeys: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun pfcount(vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun del(vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun unlink(vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun dump(key: String?): RedisFuture<ByteArray> {
        throw UnsupportedOperationException()
    }

    override fun exists(vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun expire(key: String?, seconds: Long): RedisFuture<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun expireat(key: String?, timestamp: Date?): RedisFuture<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun expireat(key: String?, timestamp: Long): RedisFuture<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun keys(pattern: String?): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun keys(channel: KeyStreamingChannel<String>?, pattern: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun migrate(host: String?, port: Int, key: String?, db: Int, timeout: Long): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun migrate(
        host: String?,
        port: Int,
        db: Int,
        timeout: Long,
        migrateArgs: MigrateArgs<String>?
    ): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun move(key: String?, db: Int): RedisFuture<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun objectEncoding(key: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun objectIdletime(key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun objectRefcount(key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun persist(key: String?): RedisFuture<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun pexpire(key: String?, milliseconds: Long): RedisFuture<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun pexpireat(key: String?, timestamp: Date?): RedisFuture<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun pexpireat(key: String?, timestamp: Long): RedisFuture<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun pttl(key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun randomkey(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun rename(key: String?, newKey: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun renamenx(key: String?, newKey: String?): RedisFuture<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun restore(key: String?, ttl: Long, value: ByteArray?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun restore(key: String?, value: ByteArray?, args: RestoreArgs?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun sort(key: String?): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun sort(channel: ValueStreamingChannel<String>?, key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun sort(key: String?, sortArgs: SortArgs?): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun sort(channel: ValueStreamingChannel<String>?, key: String?, sortArgs: SortArgs?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun sortStore(key: String?, sortArgs: SortArgs?, destination: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun touch(vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun ttl(key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun type(key: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun scan(): RedisFuture<KeyScanCursor<String>> {
        throw UnsupportedOperationException()
    }

    override fun scan(scanArgs: ScanArgs?): RedisFuture<KeyScanCursor<String>> {
        throw UnsupportedOperationException()
    }

    override fun scan(scanCursor: ScanCursor?, scanArgs: ScanArgs?): RedisFuture<KeyScanCursor<String>> {
        throw UnsupportedOperationException()
    }

    override fun scan(scanCursor: ScanCursor?): RedisFuture<KeyScanCursor<String>> {
        throw UnsupportedOperationException()
    }

    override fun scan(channel: KeyStreamingChannel<String>?): RedisFuture<StreamScanCursor> {
        throw UnsupportedOperationException()
    }

    override fun scan(channel: KeyStreamingChannel<String>?, scanArgs: ScanArgs?): RedisFuture<StreamScanCursor> {
        throw UnsupportedOperationException()
    }

    override fun scan(
        channel: KeyStreamingChannel<String>?,
        scanCursor: ScanCursor?,
        scanArgs: ScanArgs?
    ): RedisFuture<StreamScanCursor> {
        throw UnsupportedOperationException()
    }

    override fun scan(channel: KeyStreamingChannel<String>?, scanCursor: ScanCursor?): RedisFuture<StreamScanCursor> {
        throw UnsupportedOperationException()
    }

    override fun blpop(timeout: Long, vararg keys: String?): RedisFuture<KeyValue<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun brpop(timeout: Long, vararg keys: String?): RedisFuture<KeyValue<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun brpoplpush(timeout: Long, source: String?, destination: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun lindex(key: String?, index: Long): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun linsert(key: String?, before: Boolean, pivot: String?, value: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun llen(key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun lpop(key: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun lpos(key: String?, value: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun lpos(key: String?, value: String?, args: LPosArgs?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun lpos(key: String?, value: String?, count: Int): RedisFuture<MutableList<Long>> {
        throw UnsupportedOperationException()
    }

    override fun lpos(key: String?, value: String?, count: Int, args: LPosArgs?): RedisFuture<MutableList<Long>> {
        throw UnsupportedOperationException()
    }

    override fun lpush(key: String?, vararg values: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun lpushx(key: String?, vararg values: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun lrange(key: String?, start: Long, stop: Long): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun lrange(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        start: Long,
        stop: Long
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun lrem(key: String?, count: Long, value: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun lset(key: String?, index: Long, value: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun ltrim(key: String?, start: Long, stop: Long): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun rpop(key: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun rpoplpush(source: String?, destination: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun rpush(key: String?, vararg values: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun rpushx(key: String?, vararg values: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> eval(script: String?, type: ScriptOutputType?, vararg keys: String?): RedisFuture<T> {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> eval(script: ByteArray?, type: ScriptOutputType?, vararg keys: String?): RedisFuture<T> {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> eval(
        script: String?,
        type: ScriptOutputType?,
        keys: Array<out String>?,
        vararg values: String?
    ): RedisFuture<T> {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> eval(
        script: ByteArray?,
        type: ScriptOutputType?,
        keys: Array<out String>?,
        vararg values: String?
    ): RedisFuture<T> {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> evalsha(digest: String?, type: ScriptOutputType?, vararg keys: String?): RedisFuture<T> {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> evalsha(
        digest: String?,
        type: ScriptOutputType?,
        keys: Array<out String>?,
        vararg values: String?
    ): RedisFuture<T> {
        throw UnsupportedOperationException()
    }

    override fun scriptExists(vararg digests: String?): RedisFuture<MutableList<Boolean>> {
        throw UnsupportedOperationException()
    }

    override fun scriptFlush(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun scriptKill(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun scriptLoad(script: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun scriptLoad(script: ByteArray?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun digest(script: String?): String {
        throw UnsupportedOperationException()
    }

    override fun digest(script: ByteArray?): String {
        throw UnsupportedOperationException()
    }

    override fun bgrewriteaof(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun bgsave(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clientCaching(enabled: Boolean): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clientGetname(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clientGetredir(): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun clientId(): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun clientKill(addr: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clientKill(killArgs: KillArgs?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun clientList(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clientPause(timeout: Long): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clientSetname(name: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clientTracking(args: TrackingArgs?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clientUnblock(id: Long, type: UnblockType?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun command(): RedisFuture<MutableList<Any>> {
        throw UnsupportedOperationException()
    }

    override fun commandCount(): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun commandInfo(vararg commands: String?): RedisFuture<MutableList<Any>> {
        throw UnsupportedOperationException()
    }

    override fun commandInfo(vararg commands: CommandType?): RedisFuture<MutableList<Any>> {
        throw UnsupportedOperationException()
    }

    override fun configGet(parameter: String?): RedisFuture<MutableMap<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun configResetstat(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun configRewrite(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun configSet(parameter: String?, value: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun dbsize(): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun debugCrashAndRecover(delay: Long?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun debugHtstats(db: Int): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun debugObject(key: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun debugOom() {
        throw UnsupportedOperationException()
    }

    override fun debugReload(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun debugRestart(delay: Long?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun debugSdslen(key: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun debugSegfault() {
        throw UnsupportedOperationException()
    }

    override fun flushall(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun flushallAsync(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun flushdb(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun flushdbAsync(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun info(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun info(section: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun lastsave(): RedisFuture<Date> {
        throw UnsupportedOperationException()
    }

    override fun memoryUsage(key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun save(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun shutdown(save: Boolean) {
        throw UnsupportedOperationException()
    }

    override fun slaveof(host: String?, port: Int): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun slaveofNoOne(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun slowlogGet(): RedisFuture<MutableList<Any>> {
        throw UnsupportedOperationException()
    }

    override fun slowlogGet(count: Int): RedisFuture<MutableList<Any>> {
        throw UnsupportedOperationException()
    }

    override fun slowlogLen(): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun slowlogReset(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun time(): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun sadd(key: String?, vararg members: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun scard(key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun sdiff(vararg keys: String?): RedisFuture<MutableSet<String>> {
        throw UnsupportedOperationException()
    }

    override fun sdiff(channel: ValueStreamingChannel<String>?, vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun sdiffstore(destination: String?, vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun sinter(vararg keys: String?): RedisFuture<MutableSet<String>> {
        throw UnsupportedOperationException()
    }

    override fun sinter(channel: ValueStreamingChannel<String>?, vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun sinterstore(destination: String?, vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun sismember(key: String?, member: String?): RedisFuture<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun smove(source: String?, destination: String?, member: String?): RedisFuture<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun smembers(key: String?): RedisFuture<MutableSet<String>> {
        throw UnsupportedOperationException()
    }

    override fun smembers(channel: ValueStreamingChannel<String>?, key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun spop(key: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun spop(key: String?, count: Long): RedisFuture<MutableSet<String>> {
        throw UnsupportedOperationException()
    }

    override fun srandmember(key: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun srandmember(key: String?, count: Long): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun srandmember(channel: ValueStreamingChannel<String>?, key: String?, count: Long): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun srem(key: String?, vararg members: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun sunion(vararg keys: String?): RedisFuture<MutableSet<String>> {
        throw UnsupportedOperationException()
    }

    override fun sunion(channel: ValueStreamingChannel<String>?, vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun sunionstore(destination: String?, vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun sscan(key: String?): RedisFuture<ValueScanCursor<String>> {
        throw UnsupportedOperationException()
    }

    override fun sscan(key: String?, scanArgs: ScanArgs?): RedisFuture<ValueScanCursor<String>> {
        throw UnsupportedOperationException()
    }

    override fun sscan(
        key: String?,
        scanCursor: ScanCursor?,
        scanArgs: ScanArgs?
    ): RedisFuture<ValueScanCursor<String>> {
        throw UnsupportedOperationException()
    }

    override fun sscan(key: String?, scanCursor: ScanCursor?): RedisFuture<ValueScanCursor<String>> {
        throw UnsupportedOperationException()
    }

    override fun sscan(channel: ValueStreamingChannel<String>?, key: String?): RedisFuture<StreamScanCursor> {
        throw UnsupportedOperationException()
    }

    override fun sscan(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        scanArgs: ScanArgs?
    ): RedisFuture<StreamScanCursor> {
        throw UnsupportedOperationException()
    }

    override fun sscan(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        scanCursor: ScanCursor?,
        scanArgs: ScanArgs?
    ): RedisFuture<StreamScanCursor> {
        throw UnsupportedOperationException()
    }

    override fun sscan(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        scanCursor: ScanCursor?
    ): RedisFuture<StreamScanCursor> {
        throw UnsupportedOperationException()
    }

    override fun bzpopmin(timeout: Long, vararg keys: String?): RedisFuture<KeyValue<String, ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun bzpopmax(timeout: Long, vararg keys: String?): RedisFuture<KeyValue<String, ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun zadd(key: String?, score: Double, member: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zadd(key: String?, vararg scoresAndValues: Any?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zadd(key: String?, vararg scoredValues: ScoredValue<String>?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zadd(key: String?, zAddArgs: ZAddArgs?, score: Double, member: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zadd(key: String?, zAddArgs: ZAddArgs?, vararg scoresAndValues: Any?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zadd(key: String?, zAddArgs: ZAddArgs?, vararg scoredValues: ScoredValue<String>?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zaddincr(key: String?, score: Double, member: String?): RedisFuture<Double> {
        throw UnsupportedOperationException()
    }

    override fun zaddincr(key: String?, zAddArgs: ZAddArgs?, score: Double, member: String?): RedisFuture<Double> {
        throw UnsupportedOperationException()
    }

    override fun zcard(key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zcount(key: String?, min: Double, max: Double): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zcount(key: String?, min: String?, max: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zcount(key: String?, range: Range<out Number>?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zincrby(key: String?, amount: Double, member: String?): RedisFuture<Double> {
        throw UnsupportedOperationException()
    }

    override fun zinterstore(destination: String?, vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zinterstore(destination: String?, storeArgs: ZStoreArgs?, vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zlexcount(key: String?, min: String?, max: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zlexcount(key: String?, range: Range<out String>?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zpopmin(key: String?): RedisFuture<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zpopmin(key: String?, count: Long): RedisFuture<MutableList<ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun zpopmax(key: String?): RedisFuture<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zpopmax(key: String?, count: Long): RedisFuture<MutableList<ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun zrange(key: String?, start: Long, stop: Long): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrange(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        start: Long,
        stop: Long
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrangeWithScores(
        key: String?,
        start: Long,
        stop: Long
    ): RedisFuture<MutableList<ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun zrangeWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        start: Long,
        stop: Long
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrangebylex(key: String?, min: String?, max: String?): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebylex(key: String?, range: Range<out String>?): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebylex(
        key: String?,
        min: String?,
        max: String?,
        offset: Long,
        count: Long
    ): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebylex(key: String?, range: Range<out String>?, limit: Limit?): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(key: String?, min: Double, max: Double): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(key: String?, min: String?, max: String?): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(key: String?, range: Range<out Number>?): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(
        key: String?,
        min: Double,
        max: Double,
        offset: Long,
        count: Long
    ): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(
        key: String?,
        min: String?,
        max: String?,
        offset: Long,
        count: Long
    ): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(
        key: String?,
        range: Range<out Number>?,
        limit: Limit?
    ): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        min: Double,
        max: Double
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        min: String?,
        max: String?
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        range: Range<out Number>?
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        min: Double,
        max: Double,
        offset: Long,
        count: Long
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        min: String?,
        max: String?,
        offset: Long,
        count: Long
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        range: Range<out Number>?,
        limit: Limit?
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        key: String?,
        min: Double,
        max: Double
    ): RedisFuture<MutableList<ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        key: String?,
        min: String?,
        max: String?
    ): RedisFuture<MutableList<ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        key: String?,
        range: Range<out Number>?
    ): RedisFuture<MutableList<ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        key: String?,
        min: Double,
        max: Double,
        offset: Long,
        count: Long
    ): RedisFuture<MutableList<ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        key: String?,
        min: String?,
        max: String?,
        offset: Long,
        count: Long
    ): RedisFuture<MutableList<ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        key: String?,
        range: Range<out Number>?,
        limit: Limit?
    ): RedisFuture<MutableList<ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        min: Double,
        max: Double
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        min: String?,
        max: String?
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        range: Range<out Number>?
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        min: Double,
        max: Double,
        offset: Long,
        count: Long
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        min: String?,
        max: String?,
        offset: Long,
        count: Long
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        range: Range<out Number>?,
        limit: Limit?
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrank(key: String?, member: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrem(key: String?, vararg members: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zremrangebylex(key: String?, min: String?, max: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zremrangebylex(key: String?, range: Range<out String>?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zremrangebyrank(key: String?, start: Long, stop: Long): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zremrangebyscore(key: String?, min: Double, max: Double): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zremrangebyscore(key: String?, min: String?, max: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zremrangebyscore(key: String?, range: Range<out Number>?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrevrange(key: String?, start: Long, stop: Long): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrange(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        start: Long,
        stop: Long
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangeWithScores(
        key: String?,
        start: Long,
        stop: Long
    ): RedisFuture<MutableList<ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangeWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        start: Long,
        stop: Long
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebylex(key: String?, range: Range<out String>?): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebylex(
        key: String?,
        range: Range<out String>?,
        limit: Limit?
    ): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(key: String?, max: Double, min: Double): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(key: String?, max: String?, min: String?): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(key: String?, range: Range<out Number>?): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(
        key: String?,
        max: Double,
        min: Double,
        offset: Long,
        count: Long
    ): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(
        key: String?,
        max: String?,
        min: String?,
        offset: Long,
        count: Long
    ): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(
        key: String?,
        range: Range<out Number>?,
        limit: Limit?
    ): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        max: Double,
        min: Double
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        max: String?,
        min: String?
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        range: Range<out Number>?
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        max: Double,
        min: Double,
        offset: Long,
        count: Long
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        max: String?,
        min: String?,
        offset: Long,
        count: Long
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        range: Range<out Number>?,
        limit: Limit?
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        key: String?,
        max: Double,
        min: Double
    ): RedisFuture<MutableList<ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        key: String?,
        max: String?,
        min: String?
    ): RedisFuture<MutableList<ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        key: String?,
        range: Range<out Number>?
    ): RedisFuture<MutableList<ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        key: String?,
        max: Double,
        min: Double,
        offset: Long,
        count: Long
    ): RedisFuture<MutableList<ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        key: String?,
        max: String?,
        min: String?,
        offset: Long,
        count: Long
    ): RedisFuture<MutableList<ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        key: String?,
        range: Range<out Number>?,
        limit: Limit?
    ): RedisFuture<MutableList<ScoredValue<String>>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        max: Double,
        min: Double
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        max: String?,
        min: String?
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        range: Range<out Number>?
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        max: Double,
        min: Double,
        offset: Long,
        count: Long
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        max: String?,
        min: String?,
        offset: Long,
        count: Long
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        range: Range<out Number>?,
        limit: Limit?
    ): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zrevrank(key: String?, member: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zscan(key: String?): RedisFuture<ScoredValueScanCursor<String>> {
        throw UnsupportedOperationException()
    }

    override fun zscan(key: String?, scanArgs: ScanArgs?): RedisFuture<ScoredValueScanCursor<String>> {
        throw UnsupportedOperationException()
    }

    override fun zscan(
        key: String?,
        scanCursor: ScanCursor?,
        scanArgs: ScanArgs?
    ): RedisFuture<ScoredValueScanCursor<String>> {
        throw UnsupportedOperationException()
    }

    override fun zscan(key: String?, scanCursor: ScanCursor?): RedisFuture<ScoredValueScanCursor<String>> {
        throw UnsupportedOperationException()
    }

    override fun zscan(channel: ScoredValueStreamingChannel<String>?, key: String?): RedisFuture<StreamScanCursor> {
        throw UnsupportedOperationException()
    }

    override fun zscan(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        scanArgs: ScanArgs?
    ): RedisFuture<StreamScanCursor> {
        throw UnsupportedOperationException()
    }

    override fun zscan(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        scanCursor: ScanCursor?,
        scanArgs: ScanArgs?
    ): RedisFuture<StreamScanCursor> {
        throw UnsupportedOperationException()
    }

    override fun zscan(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        scanCursor: ScanCursor?
    ): RedisFuture<StreamScanCursor> {
        throw UnsupportedOperationException()
    }

    override fun zscore(key: String?, member: String?): RedisFuture<Double> {
        throw UnsupportedOperationException()
    }

    override fun zunionstore(destination: String?, vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun zunionstore(destination: String?, storeArgs: ZStoreArgs?, vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun xack(key: String?, group: String?, vararg messageIds: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun xadd(key: String?, body: MutableMap<String, String>?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun xadd(key: String?, args: XAddArgs?, body: MutableMap<String, String>?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun xadd(key: String?, vararg keysAndValues: Any?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun xadd(key: String?, args: XAddArgs?, vararg keysAndValues: Any?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun xclaim(
        key: String?,
        consumer: Consumer<String>?,
        minIdleTime: Long,
        vararg messageIds: String?
    ): RedisFuture<MutableList<StreamMessage<String, String>>> {
        throw UnsupportedOperationException()
    }

    override fun xclaim(
        key: String?,
        consumer: Consumer<String>?,
        args: XClaimArgs?,
        vararg messageIds: String?
    ): RedisFuture<MutableList<StreamMessage<String, String>>> {
        throw UnsupportedOperationException()
    }

    override fun xdel(key: String?, vararg messageIds: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun xgroupCreate(streamOffset: XReadArgs.StreamOffset<String>?, group: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun xgroupCreate(
        streamOffset: XReadArgs.StreamOffset<String>?,
        group: String?,
        args: XGroupCreateArgs?
    ): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun xgroupDelconsumer(key: String?, consumer: Consumer<String>?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun xgroupDestroy(key: String?, group: String?): RedisFuture<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun xgroupSetid(streamOffset: XReadArgs.StreamOffset<String>?, group: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun xinfoStream(key: String?): RedisFuture<MutableList<Any>> {
        throw UnsupportedOperationException()
    }

    override fun xinfoGroups(key: String?): RedisFuture<MutableList<Any>> {
        throw UnsupportedOperationException()
    }

    override fun xinfoConsumers(key: String?, group: String?): RedisFuture<MutableList<Any>> {
        throw UnsupportedOperationException()
    }

    override fun xlen(key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun xpending(key: String?, group: String?): RedisFuture<PendingMessages> {
        throw UnsupportedOperationException()
    }

    override fun xpending(
        key: String?,
        group: String?,
        range: Range<String>?,
        limit: Limit?
    ): RedisFuture<MutableList<PendingMessage>> {
        throw UnsupportedOperationException()
    }

    override fun xpending(
        key: String?,
        consumer: Consumer<String>?,
        range: Range<String>?,
        limit: Limit?
    ): RedisFuture<MutableList<PendingMessage>> {
        throw UnsupportedOperationException()
    }

    override fun xrange(key: String?, range: Range<String>?): RedisFuture<MutableList<StreamMessage<String, String>>> {
        throw UnsupportedOperationException()
    }

    override fun xrange(
        key: String?,
        range: Range<String>?,
        limit: Limit?
    ): RedisFuture<MutableList<StreamMessage<String, String>>> {
        throw UnsupportedOperationException()
    }

    override fun xread(vararg streams: XReadArgs.StreamOffset<String>?): RedisFuture<MutableList<StreamMessage<String, String>>> {
        throw UnsupportedOperationException()
    }

    override fun xread(
        args: XReadArgs?,
        vararg streams: XReadArgs.StreamOffset<String>?
    ): RedisFuture<MutableList<StreamMessage<String, String>>> {
        throw UnsupportedOperationException()
    }

    override fun xreadgroup(
        consumer: Consumer<String>?,
        vararg streams: XReadArgs.StreamOffset<String>?
    ): RedisFuture<MutableList<StreamMessage<String, String>>> {
        throw UnsupportedOperationException()
    }

    override fun xreadgroup(
        consumer: Consumer<String>?,
        args: XReadArgs?,
        vararg streams: XReadArgs.StreamOffset<String>?
    ): RedisFuture<MutableList<StreamMessage<String, String>>> {
        throw UnsupportedOperationException()
    }

    override fun xrevrange(
        key: String?,
        range: Range<String>?
    ): RedisFuture<MutableList<StreamMessage<String, String>>> {
        throw UnsupportedOperationException()
    }

    override fun xrevrange(
        key: String?,
        range: Range<String>?,
        limit: Limit?
    ): RedisFuture<MutableList<StreamMessage<String, String>>> {
        throw UnsupportedOperationException()
    }

    override fun xtrim(key: String?, count: Long): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun xtrim(key: String?, approximateTrimming: Boolean, count: Long): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun append(key: String?, value: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun bitcount(key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun bitcount(key: String?, start: Long, end: Long): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun bitfield(key: String?, bitFieldArgs: BitFieldArgs?): RedisFuture<MutableList<Long>> {
        throw UnsupportedOperationException()
    }

    override fun bitpos(key: String?, state: Boolean): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun bitpos(key: String?, state: Boolean, start: Long): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun bitpos(key: String?, state: Boolean, start: Long, end: Long): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun bitopAnd(destination: String?, vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun bitopNot(destination: String?, source: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun bitopOr(destination: String?, vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun bitopXor(destination: String?, vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun decr(key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun decrby(key: String?, amount: Long): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun get(key: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun getbit(key: String?, offset: Long): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun getrange(key: String?, start: Long, end: Long): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun getset(key: String?, value: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun incr(key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun incrby(key: String?, amount: Long): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun incrbyfloat(key: String?, amount: Double): RedisFuture<Double> {
        throw UnsupportedOperationException()
    }

    override fun mget(vararg keys: String?): RedisFuture<MutableList<KeyValue<String, String>>> {
        throw UnsupportedOperationException()
    }

    override fun mget(channel: KeyValueStreamingChannel<String, String>?, vararg keys: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun mset(map: MutableMap<String, String>?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun msetnx(map: MutableMap<String, String>?): RedisFuture<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun set(key: String?, value: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun set(key: String?, value: String?, setArgs: SetArgs?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun setbit(key: String?, offset: Long, value: Int): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun setex(key: String?, seconds: Long, value: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun psetex(key: String?, milliseconds: Long, value: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun setnx(key: String?, value: String?): RedisFuture<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun setrange(key: String?, offset: Long, value: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun stralgoLcs(strAlgoArgs: StrAlgoArgs?): RedisFuture<StringMatchResult> {
        throw UnsupportedOperationException()
    }

    override fun strlen(key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun setTimeout(timeout: Duration?) {
        throw UnsupportedOperationException()
    }

    override fun auth(password: CharSequence?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun auth(username: String?, password: CharSequence?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterBumpepoch(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterMeet(ip: String?, port: Int): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterForget(nodeId: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterAddSlots(vararg slots: Int): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterDelSlots(vararg slots: Int): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterSetSlotNode(slot: Int, nodeId: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterSetSlotStable(slot: Int): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterSetSlotMigrating(slot: Int, nodeId: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterSetSlotImporting(slot: Int, nodeId: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterInfo(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterMyId(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterNodes(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterSlaves(nodeId: String?): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun clusterGetKeysInSlot(slot: Int, count: Int): RedisFuture<MutableList<String>> {
        throw UnsupportedOperationException()
    }

    override fun clusterCountKeysInSlot(slot: Int): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun clusterCountFailureReports(nodeId: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun clusterKeyslot(key: String?): RedisFuture<Long> {
        throw UnsupportedOperationException()
    }

    override fun clusterSaveconfig(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterSetConfigEpoch(configEpoch: Long): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterSlots(): RedisFuture<MutableList<Any>> {
        throw UnsupportedOperationException()
    }

    override fun asking(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterReplicate(nodeId: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterFailover(force: Boolean): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterReset(hard: Boolean): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterFlushslots(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun discard(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun exec(): RedisFuture<TransactionResult> {
        throw UnsupportedOperationException()
    }

    override fun multi(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun watch(vararg keys: String?): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun unwatch(): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun select(db: Int): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun swapdb(db1: Int, db2: Int): RedisFuture<String> {
        throw UnsupportedOperationException()
    }

    override fun getStatefulConnection(): StatefulRedisConnection<String, String> {
        throw UnsupportedOperationException()
    }
}