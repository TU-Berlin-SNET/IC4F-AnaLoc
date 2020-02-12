dir /a:d/b/s | findstr /e /r "services\\[a-z]*Service$" > "folder.list"
for /f "tokens=*" %%A in (folder.list) do (
    cd %%A && build.cmd && cd ../..
)