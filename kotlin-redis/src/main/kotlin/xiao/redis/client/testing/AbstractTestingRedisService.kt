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
import xiao.redis.client.service.RedisService
import java.time.Duration
import java.util.Date

/**
 *
 * @author lix wang
 */
abstract class AbstractTestingRedisService : RedisService {
    override fun publish(channel: String?, message: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun pubsubChannels(): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun pubsubChannels(channel: String?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun pubsubNumsub(vararg channels: String?): MutableMap<String, Long> {
        throw UnsupportedOperationException()
    }

    override fun pubsubNumpat(): Long {
        throw UnsupportedOperationException()
    }

    override fun echo(msg: String?): String {
        throw UnsupportedOperationException()
    }

    override fun role(): MutableList<Any> {
        throw UnsupportedOperationException()
    }

    override fun ping(): String {
        throw UnsupportedOperationException()
    }

    override fun readOnly(): String {
        throw UnsupportedOperationException()
    }

    override fun readWrite(): String {
        throw UnsupportedOperationException()
    }

    override fun quit(): String {
        throw UnsupportedOperationException()
    }

    override fun waitForReplication(replicas: Int, timeout: Long): Long {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> dispatch(type: ProtocolKeyword?, output: CommandOutput<String, String, T>?): T {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> dispatch(
        type: ProtocolKeyword?,
        output: CommandOutput<String, String, T>?,
        args: CommandArgs<String, String>?
    ): T {
        throw UnsupportedOperationException()
    }

    override fun isOpen(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun reset() {
        throw UnsupportedOperationException()
    }

    override fun geoadd(key: String?, longitude: Double, latitude: Double, member: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun geoadd(key: String?, vararg lngLatMember: Any?): Long {
        throw UnsupportedOperationException()
    }

    override fun geohash(key: String?, vararg members: String?): MutableList<Value<String>> {
        throw UnsupportedOperationException()
    }

    override fun georadius(
        key: String?,
        longitude: Double,
        latitude: Double,
        distance: Double,
        unit: GeoArgs.Unit?
    ): MutableSet<String> {
        throw UnsupportedOperationException()
    }

    override fun georadius(
        key: String?,
        longitude: Double,
        latitude: Double,
        distance: Double,
        unit: GeoArgs.Unit?,
        geoArgs: GeoArgs?
    ): MutableList<GeoWithin<String>> {
        throw UnsupportedOperationException()
    }

    override fun georadius(
        key: String?,
        longitude: Double,
        latitude: Double,
        distance: Double,
        unit: GeoArgs.Unit?,
        geoRadiusStoreArgs: GeoRadiusStoreArgs<String>?
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun georadiusbymember(
        key: String?,
        member: String?,
        distance: Double,
        unit: GeoArgs.Unit?
    ): MutableSet<String> {
        throw UnsupportedOperationException()
    }

    override fun georadiusbymember(
        key: String?,
        member: String?,
        distance: Double,
        unit: GeoArgs.Unit?,
        geoArgs: GeoArgs?
    ): MutableList<GeoWithin<String>> {
        throw UnsupportedOperationException()
    }

    override fun georadiusbymember(
        key: String?,
        member: String?,
        distance: Double,
        unit: GeoArgs.Unit?,
        geoRadiusStoreArgs: GeoRadiusStoreArgs<String>?
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun geopos(key: String?, vararg members: String?): MutableList<GeoCoordinates> {
        throw UnsupportedOperationException()
    }

    override fun geodist(key: String?, from: String?, to: String?, unit: GeoArgs.Unit?): Double {
        throw UnsupportedOperationException()
    }

    override fun hdel(key: String?, vararg fields: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun hexists(key: String?, field: String?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun hget(key: String?, field: String?): String {
        throw UnsupportedOperationException()
    }

    override fun hincrby(key: String?, field: String?, amount: Long): Long {
        throw UnsupportedOperationException()
    }

    override fun hincrbyfloat(key: String?, field: String?, amount: Double): Double {
        throw UnsupportedOperationException()
    }

    override fun hgetall(key: String?): MutableMap<String, String> {
        throw UnsupportedOperationException()
    }

    override fun hgetall(channel: KeyValueStreamingChannel<String, String>?, key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun hkeys(key: String?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun hkeys(channel: KeyStreamingChannel<String>?, key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun hlen(key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun hmget(key: String?, vararg fields: String?): MutableList<KeyValue<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun hmget(channel: KeyValueStreamingChannel<String, String>?, key: String?, vararg fields: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun hmset(key: String?, map: MutableMap<String, String>?): String {
        throw UnsupportedOperationException()
    }

    override fun hscan(key: String?): MapScanCursor<String, String> {
        throw UnsupportedOperationException()
    }

    override fun hscan(key: String?, scanArgs: ScanArgs?): MapScanCursor<String, String> {
        throw UnsupportedOperationException()
    }

    override fun hscan(key: String?, scanCursor: ScanCursor?, scanArgs: ScanArgs?): MapScanCursor<String, String> {
        throw UnsupportedOperationException()
    }

    override fun hscan(key: String?, scanCursor: ScanCursor?): MapScanCursor<String, String> {
        throw UnsupportedOperationException()
    }

    override fun hscan(channel: KeyValueStreamingChannel<String, String>?, key: String?): StreamScanCursor {
        throw UnsupportedOperationException()
    }

    override fun hscan(
        channel: KeyValueStreamingChannel<String, String>?,
        key: String?,
        scanArgs: ScanArgs?
    ): StreamScanCursor {
        throw UnsupportedOperationException()
    }

    override fun hscan(
        channel: KeyValueStreamingChannel<String, String>?,
        key: String?,
        scanCursor: ScanCursor?,
        scanArgs: ScanArgs?
    ): StreamScanCursor {
        throw UnsupportedOperationException()
    }

    override fun hscan(
        channel: KeyValueStreamingChannel<String, String>?,
        key: String?,
        scanCursor: ScanCursor?
    ): StreamScanCursor {
        throw UnsupportedOperationException()
    }

    override fun hset(key: String?, field: String?, value: String?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun hset(key: String?, map: MutableMap<String, String>?): Long {
        throw UnsupportedOperationException()
    }

    override fun hsetnx(key: String?, field: String?, value: String?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun hstrlen(key: String?, field: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun hvals(key: String?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun hvals(channel: ValueStreamingChannel<String>?, key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun pfadd(key: String?, vararg values: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun pfmerge(destkey: String?, vararg sourcekeys: String?): String {
        throw UnsupportedOperationException()
    }

    override fun pfcount(vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun del(vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun unlink(vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun dump(key: String?): ByteArray {
        throw UnsupportedOperationException()
    }

    override fun exists(vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun expire(key: String?, seconds: Long): Boolean {
        throw UnsupportedOperationException()
    }

    override fun expireat(key: String?, timestamp: Date?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun expireat(key: String?, timestamp: Long): Boolean {
        throw UnsupportedOperationException()
    }

    override fun keys(pattern: String?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun keys(channel: KeyStreamingChannel<String>?, pattern: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun migrate(host: String?, port: Int, key: String?, db: Int, timeout: Long): String {
        throw UnsupportedOperationException()
    }

    override fun migrate(host: String?, port: Int, db: Int, timeout: Long, migrateArgs: MigrateArgs<String>?): String {
        throw UnsupportedOperationException()
    }

    override fun move(key: String?, db: Int): Boolean {
        throw UnsupportedOperationException()
    }

    override fun objectEncoding(key: String?): String {
        throw UnsupportedOperationException()
    }

    override fun objectIdletime(key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun objectRefcount(key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun persist(key: String?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun pexpire(key: String?, milliseconds: Long): Boolean {
        throw UnsupportedOperationException()
    }

    override fun pexpireat(key: String?, timestamp: Date?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun pexpireat(key: String?, timestamp: Long): Boolean {
        throw UnsupportedOperationException()
    }

    override fun pttl(key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun randomkey(): String {
        throw UnsupportedOperationException()
    }

    override fun rename(key: String?, newKey: String?): String {
        throw UnsupportedOperationException()
    }

    override fun renamenx(key: String?, newKey: String?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun restore(key: String?, ttl: Long, value: ByteArray?): String {
        throw UnsupportedOperationException()
    }

    override fun restore(key: String?, value: ByteArray?, args: RestoreArgs?): String {
        throw UnsupportedOperationException()
    }

    override fun sort(key: String?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun sort(channel: ValueStreamingChannel<String>?, key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun sort(key: String?, sortArgs: SortArgs?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun sort(channel: ValueStreamingChannel<String>?, key: String?, sortArgs: SortArgs?): Long {
        throw UnsupportedOperationException()
    }

    override fun sortStore(key: String?, sortArgs: SortArgs?, destination: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun touch(vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun ttl(key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun type(key: String?): String {
        throw UnsupportedOperationException()
    }

    override fun scan(): KeyScanCursor<String> {
        throw UnsupportedOperationException()
    }

    override fun scan(scanArgs: ScanArgs?): KeyScanCursor<String> {
        throw UnsupportedOperationException()
    }

    override fun scan(scanCursor: ScanCursor?, scanArgs: ScanArgs?): KeyScanCursor<String> {
        throw UnsupportedOperationException()
    }

    override fun scan(scanCursor: ScanCursor?): KeyScanCursor<String> {
        throw UnsupportedOperationException()
    }

    override fun scan(channel: KeyStreamingChannel<String>?): StreamScanCursor {
        throw UnsupportedOperationException()
    }

    override fun scan(channel: KeyStreamingChannel<String>?, scanArgs: ScanArgs?): StreamScanCursor {
        throw UnsupportedOperationException()
    }

    override fun scan(
        channel: KeyStreamingChannel<String>?,
        scanCursor: ScanCursor?,
        scanArgs: ScanArgs?
    ): StreamScanCursor {
        throw UnsupportedOperationException()
    }

    override fun scan(channel: KeyStreamingChannel<String>?, scanCursor: ScanCursor?): StreamScanCursor {
        throw UnsupportedOperationException()
    }

    override fun blpop(timeout: Long, vararg keys: String?): KeyValue<String, String> {
        throw UnsupportedOperationException()
    }

    override fun brpop(timeout: Long, vararg keys: String?): KeyValue<String, String> {
        throw UnsupportedOperationException()
    }

    override fun brpoplpush(timeout: Long, source: String?, destination: String?): String {
        throw UnsupportedOperationException()
    }

    override fun lindex(key: String?, index: Long): String {
        throw UnsupportedOperationException()
    }

    override fun linsert(key: String?, before: Boolean, pivot: String?, value: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun llen(key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun lpop(key: String?): String {
        throw UnsupportedOperationException()
    }

    override fun lpos(key: String?, value: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun lpos(key: String?, value: String?, args: LPosArgs?): Long {
        throw UnsupportedOperationException()
    }

    override fun lpos(key: String?, value: String?, count: Int): MutableList<Long> {
        throw UnsupportedOperationException()
    }

    override fun lpos(key: String?, value: String?, count: Int, args: LPosArgs?): MutableList<Long> {
        throw UnsupportedOperationException()
    }

    override fun lpush(key: String?, vararg values: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun lpushx(key: String?, vararg values: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun lrange(key: String?, start: Long, stop: Long): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun lrange(channel: ValueStreamingChannel<String>?, key: String?, start: Long, stop: Long): Long {
        throw UnsupportedOperationException()
    }

    override fun lrem(key: String?, count: Long, value: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun lset(key: String?, index: Long, value: String?): String {
        throw UnsupportedOperationException()
    }

    override fun ltrim(key: String?, start: Long, stop: Long): String {
        throw UnsupportedOperationException()
    }

    override fun rpop(key: String?): String {
        throw UnsupportedOperationException()
    }

    override fun rpoplpush(source: String?, destination: String?): String {
        throw UnsupportedOperationException()
    }

    override fun rpush(key: String?, vararg values: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun rpushx(key: String?, vararg values: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> eval(script: String?, type: ScriptOutputType?, vararg keys: String?): T {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> eval(script: ByteArray?, type: ScriptOutputType?, vararg keys: String?): T {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> eval(
        script: String?,
        type: ScriptOutputType?,
        keys: Array<out String>?,
        vararg values: String?
    ): T {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> eval(
        script: ByteArray?,
        type: ScriptOutputType?,
        keys: Array<out String>?,
        vararg values: String?
    ): T {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> evalsha(digest: String?, type: ScriptOutputType?, vararg keys: String?): T {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> evalsha(
        digest: String?,
        type: ScriptOutputType?,
        keys: Array<out String>?,
        vararg values: String?
    ): T {
        throw UnsupportedOperationException()
    }

    override fun scriptExists(vararg digests: String?): MutableList<Boolean> {
        throw UnsupportedOperationException()
    }

    override fun scriptFlush(): String {
        throw UnsupportedOperationException()
    }

    override fun scriptKill(): String {
        throw UnsupportedOperationException()
    }

    override fun scriptLoad(script: String?): String {
        throw UnsupportedOperationException()
    }

    override fun scriptLoad(script: ByteArray?): String {
        throw UnsupportedOperationException()
    }

    override fun digest(script: String?): String {
        throw UnsupportedOperationException()
    }

    override fun digest(script: ByteArray?): String {
        throw UnsupportedOperationException()
    }

    override fun bgrewriteaof(): String {
        throw UnsupportedOperationException()
    }

    override fun bgsave(): String {
        throw UnsupportedOperationException()
    }

    override fun clientCaching(enabled: Boolean): String {
        throw UnsupportedOperationException()
    }

    override fun clientGetname(): String {
        throw UnsupportedOperationException()
    }

    override fun clientGetredir(): Long {
        throw UnsupportedOperationException()
    }

    override fun clientId(): Long {
        throw UnsupportedOperationException()
    }

    override fun clientKill(addr: String?): String {
        throw UnsupportedOperationException()
    }

    override fun clientKill(killArgs: KillArgs?): Long {
        throw UnsupportedOperationException()
    }

    override fun clientList(): String {
        throw UnsupportedOperationException()
    }

    override fun clientPause(timeout: Long): String {
        throw UnsupportedOperationException()
    }

    override fun clientSetname(name: String?): String {
        throw UnsupportedOperationException()
    }

    override fun clientTracking(args: TrackingArgs?): String {
        throw UnsupportedOperationException()
    }

    override fun clientUnblock(id: Long, type: UnblockType?): Long {
        throw UnsupportedOperationException()
    }

    override fun command(): MutableList<Any> {
        throw UnsupportedOperationException()
    }

    override fun commandCount(): Long {
        throw UnsupportedOperationException()
    }

    override fun commandInfo(vararg commands: String?): MutableList<Any> {
        throw UnsupportedOperationException()
    }

    override fun commandInfo(vararg commands: CommandType?): MutableList<Any> {
        throw UnsupportedOperationException()
    }

    override fun configGet(parameter: String?): MutableMap<String, String> {
        throw UnsupportedOperationException()
    }

    override fun configResetstat(): String {
        throw UnsupportedOperationException()
    }

    override fun configRewrite(): String {
        throw UnsupportedOperationException()
    }

    override fun configSet(parameter: String?, value: String?): String {
        throw UnsupportedOperationException()
    }

    override fun dbsize(): Long {
        throw UnsupportedOperationException()
    }

    override fun debugCrashAndRecover(delay: Long?): String {
        throw UnsupportedOperationException()
    }

    override fun debugHtstats(db: Int): String {
        throw UnsupportedOperationException()
    }

    override fun debugObject(key: String?): String {
        throw UnsupportedOperationException()
    }

    override fun debugOom() {
        throw UnsupportedOperationException()
    }

    override fun debugReload(): String {
        throw UnsupportedOperationException()
    }

    override fun debugRestart(delay: Long?): String {
        throw UnsupportedOperationException()
    }

    override fun debugSdslen(key: String?): String {
        throw UnsupportedOperationException()
    }

    override fun debugSegfault() {
        throw UnsupportedOperationException()
    }

    override fun flushall(): String {
        throw UnsupportedOperationException()
    }

    override fun flushallAsync(): String {
        throw UnsupportedOperationException()
    }

    override fun flushdb(): String {
        throw UnsupportedOperationException()
    }

    override fun flushdbAsync(): String {
        throw UnsupportedOperationException()
    }

    override fun info(): String {
        throw UnsupportedOperationException()
    }

    override fun info(section: String?): String {
        throw UnsupportedOperationException()
    }

    override fun lastsave(): Date {
        throw UnsupportedOperationException()
    }

    override fun memoryUsage(key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun save(): String {
        throw UnsupportedOperationException()
    }

    override fun shutdown(save: Boolean) {
        throw UnsupportedOperationException()
    }

    override fun slaveof(host: String?, port: Int): String {
        throw UnsupportedOperationException()
    }

    override fun slaveofNoOne(): String {
        throw UnsupportedOperationException()
    }

    override fun slowlogGet(): MutableList<Any> {
        throw UnsupportedOperationException()
    }

    override fun slowlogGet(count: Int): MutableList<Any> {
        throw UnsupportedOperationException()
    }

    override fun slowlogLen(): Long {
        throw UnsupportedOperationException()
    }

    override fun slowlogReset(): String {
        throw UnsupportedOperationException()
    }

    override fun time(): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun sadd(key: String?, vararg members: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun scard(key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun sdiff(vararg keys: String?): MutableSet<String> {
        throw UnsupportedOperationException()
    }

    override fun sdiff(channel: ValueStreamingChannel<String>?, vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun sdiffstore(destination: String?, vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun sinter(vararg keys: String?): MutableSet<String> {
        throw UnsupportedOperationException()
    }

    override fun sinter(channel: ValueStreamingChannel<String>?, vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun sinterstore(destination: String?, vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun sismember(key: String?, member: String?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun smove(source: String?, destination: String?, member: String?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun smembers(key: String?): MutableSet<String> {
        throw UnsupportedOperationException()
    }

    override fun smembers(channel: ValueStreamingChannel<String>?, key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun spop(key: String?): String {
        throw UnsupportedOperationException()
    }

    override fun spop(key: String?, count: Long): MutableSet<String> {
        throw UnsupportedOperationException()
    }

    override fun srandmember(key: String?): String {
        throw UnsupportedOperationException()
    }

    override fun srandmember(key: String?, count: Long): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun srandmember(channel: ValueStreamingChannel<String>?, key: String?, count: Long): Long {
        throw UnsupportedOperationException()
    }

    override fun srem(key: String?, vararg members: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun sunion(vararg keys: String?): MutableSet<String> {
        throw UnsupportedOperationException()
    }

    override fun sunion(channel: ValueStreamingChannel<String>?, vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun sunionstore(destination: String?, vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun sscan(key: String?): ValueScanCursor<String> {
        throw UnsupportedOperationException()
    }

    override fun sscan(key: String?, scanArgs: ScanArgs?): ValueScanCursor<String> {
        throw UnsupportedOperationException()
    }

    override fun sscan(key: String?, scanCursor: ScanCursor?, scanArgs: ScanArgs?): ValueScanCursor<String> {
        throw UnsupportedOperationException()
    }

    override fun sscan(key: String?, scanCursor: ScanCursor?): ValueScanCursor<String> {
        throw UnsupportedOperationException()
    }

    override fun sscan(channel: ValueStreamingChannel<String>?, key: String?): StreamScanCursor {
        throw UnsupportedOperationException()
    }

    override fun sscan(channel: ValueStreamingChannel<String>?, key: String?, scanArgs: ScanArgs?): StreamScanCursor {
        throw UnsupportedOperationException()
    }

    override fun sscan(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        scanCursor: ScanCursor?,
        scanArgs: ScanArgs?
    ): StreamScanCursor {
        throw UnsupportedOperationException()
    }

    override fun sscan(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        scanCursor: ScanCursor?
    ): StreamScanCursor {
        throw UnsupportedOperationException()
    }

    override fun bzpopmin(timeout: Long, vararg keys: String?): KeyValue<String, ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun bzpopmax(timeout: Long, vararg keys: String?): KeyValue<String, ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zadd(key: String?, score: Double, member: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun zadd(key: String?, vararg scoresAndValues: Any?): Long {
        throw UnsupportedOperationException()
    }

    override fun zadd(key: String?, vararg scoredValues: ScoredValue<String>?): Long {
        throw UnsupportedOperationException()
    }

    override fun zadd(key: String?, zAddArgs: ZAddArgs?, score: Double, member: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun zadd(key: String?, zAddArgs: ZAddArgs?, vararg scoresAndValues: Any?): Long {
        throw UnsupportedOperationException()
    }

    override fun zadd(key: String?, zAddArgs: ZAddArgs?, vararg scoredValues: ScoredValue<String>?): Long {
        throw UnsupportedOperationException()
    }

    override fun zaddincr(key: String?, score: Double, member: String?): Double {
        throw UnsupportedOperationException()
    }

    override fun zaddincr(key: String?, zAddArgs: ZAddArgs?, score: Double, member: String?): Double {
        throw UnsupportedOperationException()
    }

    override fun zcard(key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun zcount(key: String?, min: Double, max: Double): Long {
        throw UnsupportedOperationException()
    }

    override fun zcount(key: String?, min: String?, max: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun zcount(key: String?, range: Range<out Number>?): Long {
        throw UnsupportedOperationException()
    }

    override fun zincrby(key: String?, amount: Double, member: String?): Double {
        throw UnsupportedOperationException()
    }

    override fun zinterstore(destination: String?, vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun zinterstore(destination: String?, storeArgs: ZStoreArgs?, vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun zlexcount(key: String?, min: String?, max: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun zlexcount(key: String?, range: Range<out String>?): Long {
        throw UnsupportedOperationException()
    }

    override fun zpopmin(key: String?): ScoredValue<String> {
        throw UnsupportedOperationException()
    }

    override fun zpopmin(key: String?, count: Long): MutableList<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zpopmax(key: String?): ScoredValue<String> {
        throw UnsupportedOperationException()
    }

    override fun zpopmax(key: String?, count: Long): MutableList<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrange(key: String?, start: Long, stop: Long): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrange(channel: ValueStreamingChannel<String>?, key: String?, start: Long, stop: Long): Long {
        throw UnsupportedOperationException()
    }

    override fun zrangeWithScores(key: String?, start: Long, stop: Long): MutableList<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrangeWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        start: Long,
        stop: Long
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrangebylex(key: String?, min: String?, max: String?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrangebylex(key: String?, range: Range<out String>?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrangebylex(key: String?, min: String?, max: String?, offset: Long, count: Long): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrangebylex(key: String?, range: Range<out String>?, limit: Limit?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(key: String?, min: Double, max: Double): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(key: String?, min: String?, max: String?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(key: String?, range: Range<out Number>?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(key: String?, min: Double, max: Double, offset: Long, count: Long): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(
        key: String?,
        min: String?,
        max: String?,
        offset: Long,
        count: Long
    ): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(key: String?, range: Range<out Number>?, limit: Limit?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(channel: ValueStreamingChannel<String>?, key: String?, min: Double, max: Double): Long {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        min: String?,
        max: String?
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(channel: ValueStreamingChannel<String>?, key: String?, range: Range<out Number>?): Long {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        min: Double,
        max: Double,
        offset: Long,
        count: Long
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        min: String?,
        max: String?,
        offset: Long,
        count: Long
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        range: Range<out Number>?,
        limit: Limit?
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(key: String?, min: Double, max: Double): MutableList<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(key: String?, min: String?, max: String?): MutableList<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(key: String?, range: Range<out Number>?): MutableList<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        key: String?,
        min: Double,
        max: Double,
        offset: Long,
        count: Long
    ): MutableList<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        key: String?,
        min: String?,
        max: String?,
        offset: Long,
        count: Long
    ): MutableList<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        key: String?,
        range: Range<out Number>?,
        limit: Limit?
    ): MutableList<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        min: Double,
        max: Double
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        min: String?,
        max: String?
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        range: Range<out Number>?
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        min: Double,
        max: Double,
        offset: Long,
        count: Long
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        min: String?,
        max: String?,
        offset: Long,
        count: Long
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        range: Range<out Number>?,
        limit: Limit?
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrank(key: String?, member: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun zrem(key: String?, vararg members: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun zremrangebylex(key: String?, min: String?, max: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun zremrangebylex(key: String?, range: Range<out String>?): Long {
        throw UnsupportedOperationException()
    }

    override fun zremrangebyrank(key: String?, start: Long, stop: Long): Long {
        throw UnsupportedOperationException()
    }

    override fun zremrangebyscore(key: String?, min: Double, max: Double): Long {
        throw UnsupportedOperationException()
    }

    override fun zremrangebyscore(key: String?, min: String?, max: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun zremrangebyscore(key: String?, range: Range<out Number>?): Long {
        throw UnsupportedOperationException()
    }

    override fun zrevrange(key: String?, start: Long, stop: Long): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrevrange(channel: ValueStreamingChannel<String>?, key: String?, start: Long, stop: Long): Long {
        throw UnsupportedOperationException()
    }

    override fun zrevrangeWithScores(key: String?, start: Long, stop: Long): MutableList<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangeWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        start: Long,
        stop: Long
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebylex(key: String?, range: Range<out String>?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebylex(key: String?, range: Range<out String>?, limit: Limit?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(key: String?, max: Double, min: Double): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(key: String?, max: String?, min: String?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(key: String?, range: Range<out Number>?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(
        key: String?,
        max: Double,
        min: Double,
        offset: Long,
        count: Long
    ): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(
        key: String?,
        max: String?,
        min: String?,
        offset: Long,
        count: Long
    ): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(key: String?, range: Range<out Number>?, limit: Limit?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        max: Double,
        min: Double
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        max: String?,
        min: String?
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        range: Range<out Number>?
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        max: Double,
        min: Double,
        offset: Long,
        count: Long
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        max: String?,
        min: String?,
        offset: Long,
        count: Long
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscore(
        channel: ValueStreamingChannel<String>?,
        key: String?,
        range: Range<out Number>?,
        limit: Limit?
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(key: String?, max: Double, min: Double): MutableList<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        key: String?,
        max: String?,
        min: String?
    ): MutableList<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(key: String?, range: Range<out Number>?): MutableList<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        key: String?,
        max: Double,
        min: Double,
        offset: Long,
        count: Long
    ): MutableList<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        key: String?,
        max: String?,
        min: String?,
        offset: Long,
        count: Long
    ): MutableList<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        key: String?,
        range: Range<out Number>?,
        limit: Limit?
    ): MutableList<ScoredValue<String>> {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        max: Double,
        min: Double
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        max: String?,
        min: String?
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        range: Range<out Number>?
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        max: Double,
        min: Double,
        offset: Long,
        count: Long
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        max: String?,
        min: String?,
        offset: Long,
        count: Long
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrevrangebyscoreWithScores(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        range: Range<out Number>?,
        limit: Limit?
    ): Long {
        throw UnsupportedOperationException()
    }

    override fun zrevrank(key: String?, member: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun zscan(key: String?): ScoredValueScanCursor<String> {
        throw UnsupportedOperationException()
    }

    override fun zscan(key: String?, scanArgs: ScanArgs?): ScoredValueScanCursor<String> {
        throw UnsupportedOperationException()
    }

    override fun zscan(key: String?, scanCursor: ScanCursor?, scanArgs: ScanArgs?): ScoredValueScanCursor<String> {
        throw UnsupportedOperationException()
    }

    override fun zscan(key: String?, scanCursor: ScanCursor?): ScoredValueScanCursor<String> {
        throw UnsupportedOperationException()
    }

    override fun zscan(channel: ScoredValueStreamingChannel<String>?, key: String?): StreamScanCursor {
        throw UnsupportedOperationException()
    }

    override fun zscan(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        scanArgs: ScanArgs?
    ): StreamScanCursor {
        throw UnsupportedOperationException()
    }

    override fun zscan(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        scanCursor: ScanCursor?,
        scanArgs: ScanArgs?
    ): StreamScanCursor {
        throw UnsupportedOperationException()
    }

    override fun zscan(
        channel: ScoredValueStreamingChannel<String>?,
        key: String?,
        scanCursor: ScanCursor?
    ): StreamScanCursor {
        throw UnsupportedOperationException()
    }

    override fun zscore(key: String?, member: String?): Double {
        throw UnsupportedOperationException()
    }

    override fun zunionstore(destination: String?, vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun zunionstore(destination: String?, storeArgs: ZStoreArgs?, vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun xack(key: String?, group: String?, vararg messageIds: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun xadd(key: String?, body: MutableMap<String, String>?): String {
        throw UnsupportedOperationException()
    }

    override fun xadd(key: String?, args: XAddArgs?, body: MutableMap<String, String>?): String {
        throw UnsupportedOperationException()
    }

    override fun xadd(key: String?, vararg keysAndValues: Any?): String {
        throw UnsupportedOperationException()
    }

    override fun xadd(key: String?, args: XAddArgs?, vararg keysAndValues: Any?): String {
        throw UnsupportedOperationException()
    }

    override fun xclaim(
        key: String?,
        consumer: Consumer<String>?,
        minIdleTime: Long,
        vararg messageIds: String?
    ): MutableList<StreamMessage<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun xclaim(
        key: String?,
        consumer: Consumer<String>?,
        args: XClaimArgs?,
        vararg messageIds: String?
    ): MutableList<StreamMessage<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun xdel(key: String?, vararg messageIds: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun xgroupCreate(streamOffset: XReadArgs.StreamOffset<String>?, group: String?): String {
        throw UnsupportedOperationException()
    }

    override fun xgroupCreate(
        streamOffset: XReadArgs.StreamOffset<String>?,
        group: String?,
        args: XGroupCreateArgs?
    ): String {
        throw UnsupportedOperationException()
    }

    override fun xgroupDelconsumer(key: String?, consumer: Consumer<String>?): Long {
        throw UnsupportedOperationException()
    }

    override fun xgroupDestroy(key: String?, group: String?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun xgroupSetid(streamOffset: XReadArgs.StreamOffset<String>?, group: String?): String {
        throw UnsupportedOperationException()
    }

    override fun xinfoStream(key: String?): MutableList<Any> {
        throw UnsupportedOperationException()
    }

    override fun xinfoGroups(key: String?): MutableList<Any> {
        throw UnsupportedOperationException()
    }

    override fun xinfoConsumers(key: String?, group: String?): MutableList<Any> {
        throw UnsupportedOperationException()
    }

    override fun xlen(key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun xpending(key: String?, group: String?): PendingMessages {
        throw UnsupportedOperationException()
    }

    override fun xpending(
        key: String?,
        group: String?,
        range: Range<String>?,
        limit: Limit?
    ): MutableList<PendingMessage> {
        throw UnsupportedOperationException()
    }

    override fun xpending(
        key: String?,
        consumer: Consumer<String>?,
        range: Range<String>?,
        limit: Limit?
    ): MutableList<PendingMessage> {
        throw UnsupportedOperationException()
    }

    override fun xrange(key: String?, range: Range<String>?): MutableList<StreamMessage<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun xrange(
        key: String?,
        range: Range<String>?,
        limit: Limit?
    ): MutableList<StreamMessage<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun xread(vararg streams: XReadArgs.StreamOffset<String>?): MutableList<StreamMessage<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun xread(
        args: XReadArgs?,
        vararg streams: XReadArgs.StreamOffset<String>?
    ): MutableList<StreamMessage<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun xreadgroup(
        consumer: Consumer<String>?,
        vararg streams: XReadArgs.StreamOffset<String>?
    ): MutableList<StreamMessage<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun xreadgroup(
        consumer: Consumer<String>?,
        args: XReadArgs?,
        vararg streams: XReadArgs.StreamOffset<String>?
    ): MutableList<StreamMessage<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun xrevrange(key: String?, range: Range<String>?): MutableList<StreamMessage<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun xrevrange(
        key: String?,
        range: Range<String>?,
        limit: Limit?
    ): MutableList<StreamMessage<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun xtrim(key: String?, count: Long): Long {
        throw UnsupportedOperationException()
    }

    override fun xtrim(key: String?, approximateTrimming: Boolean, count: Long): Long {
        throw UnsupportedOperationException()
    }

    override fun append(key: String?, value: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun bitcount(key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun bitcount(key: String?, start: Long, end: Long): Long {
        throw UnsupportedOperationException()
    }

    override fun bitfield(key: String?, bitFieldArgs: BitFieldArgs?): MutableList<Long> {
        throw UnsupportedOperationException()
    }

    override fun bitpos(key: String?, state: Boolean): Long {
        throw UnsupportedOperationException()
    }

    override fun bitpos(key: String?, state: Boolean, start: Long): Long {
        throw UnsupportedOperationException()
    }

    override fun bitpos(key: String?, state: Boolean, start: Long, end: Long): Long {
        throw UnsupportedOperationException()
    }

    override fun bitopAnd(destination: String?, vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun bitopNot(destination: String?, source: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun bitopOr(destination: String?, vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun bitopXor(destination: String?, vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun decr(key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun decrby(key: String?, amount: Long): Long {
        throw UnsupportedOperationException()
    }

    override fun get(key: String?): String? {
        throw UnsupportedOperationException()
    }

    override fun getbit(key: String?, offset: Long): Long {
        throw UnsupportedOperationException()
    }

    override fun getrange(key: String?, start: Long, end: Long): String {
        throw UnsupportedOperationException()
    }

    override fun getset(key: String?, value: String?): String {
        throw UnsupportedOperationException()
    }

    override fun incr(key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun incrby(key: String?, amount: Long): Long {
        throw UnsupportedOperationException()
    }

    override fun incrbyfloat(key: String?, amount: Double): Double {
        throw UnsupportedOperationException()
    }

    override fun mget(vararg keys: String?): MutableList<KeyValue<String, String>> {
        throw UnsupportedOperationException()
    }

    override fun mget(channel: KeyValueStreamingChannel<String, String>?, vararg keys: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun mset(map: MutableMap<String, String>?): String {
        throw UnsupportedOperationException()
    }

    override fun msetnx(map: MutableMap<String, String>?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun set(key: String?, value: String?): String {
        throw UnsupportedOperationException()
    }

    override fun set(key: String?, value: String?, setArgs: SetArgs?): String {
        throw UnsupportedOperationException()
    }

    override fun setbit(key: String?, offset: Long, value: Int): Long {
        throw UnsupportedOperationException()
    }

    override fun setex(key: String?, seconds: Long, value: String?): String {
        throw UnsupportedOperationException()
    }

    override fun psetex(key: String?, milliseconds: Long, value: String?): String {
        throw UnsupportedOperationException()
    }

    override fun setnx(key: String?, value: String?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun setrange(key: String?, offset: Long, value: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun stralgoLcs(strAlgoArgs: StrAlgoArgs?): StringMatchResult {
        throw UnsupportedOperationException()
    }

    override fun strlen(key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun setTimeout(timeout: Duration?) {
        throw UnsupportedOperationException()
    }

    override fun auth(password: CharSequence?): String {
        throw UnsupportedOperationException()
    }

    override fun auth(username: String?, password: CharSequence?): String {
        throw UnsupportedOperationException()
    }

    override fun clusterBumpepoch(): String {
        throw UnsupportedOperationException()
    }

    override fun clusterMeet(ip: String?, port: Int): String {
        throw UnsupportedOperationException()
    }

    override fun clusterForget(nodeId: String?): String {
        throw UnsupportedOperationException()
    }

    override fun clusterAddSlots(vararg slots: Int): String {
        throw UnsupportedOperationException()
    }

    override fun clusterDelSlots(vararg slots: Int): String {
        throw UnsupportedOperationException()
    }

    override fun clusterSetSlotNode(slot: Int, nodeId: String?): String {
        throw UnsupportedOperationException()
    }

    override fun clusterSetSlotStable(slot: Int): String {
        throw UnsupportedOperationException()
    }

    override fun clusterSetSlotMigrating(slot: Int, nodeId: String?): String {
        throw UnsupportedOperationException()
    }

    override fun clusterSetSlotImporting(slot: Int, nodeId: String?): String {
        throw UnsupportedOperationException()
    }

    override fun clusterInfo(): String {
        throw UnsupportedOperationException()
    }

    override fun clusterMyId(): String {
        throw UnsupportedOperationException()
    }

    override fun clusterNodes(): String {
        throw UnsupportedOperationException()
    }

    override fun clusterSlaves(nodeId: String?): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterGetKeysInSlot(slot: Int, count: Int): MutableList<String> {
        throw UnsupportedOperationException()
    }

    override fun clusterCountKeysInSlot(slot: Int): Long {
        throw UnsupportedOperationException()
    }

    override fun clusterCountFailureReports(nodeId: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun clusterKeyslot(key: String?): Long {
        throw UnsupportedOperationException()
    }

    override fun clusterSaveconfig(): String {
        throw UnsupportedOperationException()
    }

    override fun clusterSetConfigEpoch(configEpoch: Long): String {
        throw UnsupportedOperationException()
    }

    override fun clusterSlots(): MutableList<Any> {
        throw UnsupportedOperationException()
    }

    override fun asking(): String {
        throw UnsupportedOperationException()
    }

    override fun clusterReplicate(nodeId: String?): String {
        throw UnsupportedOperationException()
    }

    override fun clusterFailover(force: Boolean): String {
        throw UnsupportedOperationException()
    }

    override fun clusterReset(hard: Boolean): String {
        throw UnsupportedOperationException()
    }

    override fun clusterFlushslots(): String {
        throw UnsupportedOperationException()
    }

    override fun discard(): String {
        throw UnsupportedOperationException()
    }

    override fun exec(): TransactionResult {
        throw UnsupportedOperationException()
    }

    override fun multi(): String {
        throw UnsupportedOperationException()
    }

    override fun watch(vararg keys: String?): String {
        throw UnsupportedOperationException()
    }

    override fun unwatch(): String {
        throw UnsupportedOperationException()
    }

    override fun select(db: Int): String {
        throw UnsupportedOperationException()
    }

    override fun swapdb(db1: Int, db2: Int): String {
        throw UnsupportedOperationException()
    }

    override fun getStatefulConnection(): StatefulRedisConnection<String, String> {
        throw UnsupportedOperationException()
    }
}