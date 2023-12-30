local reply = redis.pcall('ECHO', unpack(ARGV))
if reply['err'] ~= nil then
    -- Handle the error sometime, but for now just log it
    redis.log(redis.LOG_WARNING, reply['err'])
    reply['err'] = 'ERR Something is wrong, but no worries, everything is under control'
end
return reply