# Chatgpt-kt-bot

a chatgpt bot for slack.

## Build
- `jdk17`
- `gradle`

```shell
$ just help
Available recipes:
    help
    clean
    build
    b          # alias for `build`
    run
    deploy
    d          # alias for `deploy`
    native
    run-native

$ just build
$ just run
```

## QuickStart

```shell
docker run  \
--name chatgpt-bot \
-v  "your-config-home/config:/opt/chatgpt-kt-bot/config" \
-p your-expose-port:10003 \
-d  \
razielez/chatgpt-kt-bot
```

## Ref
- [openai doc](https://platform.openai.com/docs/api-reference/chat/create)
