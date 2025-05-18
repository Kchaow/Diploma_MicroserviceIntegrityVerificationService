db.createUser({
    user: "master",
    pwd: "master",
    roles: [
        {
            role: "readWrite",
            db: "graphs"
        }
    ]
});
