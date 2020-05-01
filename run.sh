while true
do
    curl -H "Content-Type: application/json" \
        -H "Authorization: Bearer a623be3c-1de4-47b1-94cb-899820f21a05" \
        --request POST \
       --data '{"typeId":1, "student":"katy", "value":10, "subjectId":"so"}' \
    http://localhost:8080/assignment/grades
    echo "Run"
done
