set -e

mongosh --username "$MONGO_INITDB_ROOT_USERNAME" --password "$MONGO_INITDB_ROOT_PASSWORD" --authenticationDatabase admin <<EOF
use topic_read_db
db.createUser({
  user: "$MONGO_TOPIC_READ_SERVICE",
  pwd: "$MONGO_TOPIC_READ_PASSWORD",
  roles: [ { role: "readWrite", db: "topic_read_db" } ]
})

EOF
