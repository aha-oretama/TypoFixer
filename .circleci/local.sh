curl --user ${CIRCLE_TOKEN}: \
  --request POST \
  --form revision=aac8818e5aa4245041c2449cc891ba2c02a3f210 \
  --form config=@.circleci/config.yml \
  --form notify=false \
    https://circleci.com/api/v1.1/project/github/aha-oretama/TypoFixer/tree/master
