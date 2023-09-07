# AndroidRsync

example:

```shell
adb forward tcp:6010 tcp:1873
rsync -av --progress --stats ./test.txt rsync://localhost:6010/root/data/user/0/com.complexzeng.androidrsync/cache
```

`test.txt` will show at `/data/user/0/com.complexzeng.androidrsync/cache`

