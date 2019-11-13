Feature: Link
  Background:
    * url karate.properties['server.baseurl']
    * call read('reset.feature')

  Scenario: store link with valid specifications should return a 204
    Given path '/api/link/supplychainid'
    And request {"signature":{"keyId":"30af4282a269639cf72e2903d43cd7510d06c32df37a349c7fa4157373e63f09","signature":"20b02bc3380fcb745f13bbc1ff06607b2b76e24960bdf56d77cff2769b7c603a9f255530d98342b4120a9954b4adf8f562dd1005f4c822a5f4ea1b811e67712c10843efd3689ccd795eb71c7753b4208daeade75a2d6967e98f7652565ba4ab530222757d2f7e09be61f0940e01a81b96a33df41634a5a8ef3c7fd43cd2f0f1b5cfb2ce089c6d727ce56cc0646a3cf3897e218ac91280cef92dcbd16b532366b53e5da7d7b1aaf4130c119672506a42f95c30f84df55dab69339c8a9487eb7b2fa12c0cd1b853435326c747f52489599db4f26995a6ce4e356f60d90ad9ff7d9fa92cfc00acc07e3e2bda25d1f69b34f9178ec96eee3455eac8d75d127137ad3166fa37f4995b8ac41c7afdbb5f98ee3d4a75db126a877d394e93694f914d46f783cb0a29af2e298d6e187e3bd123d62026fe86ae0a4d0740951802906c1c5fa7dc8195fc493ab39c84ade7f2c2792275083df32ba2002bb5a115a848175d59ca7ec704fecf93201cd0c9f4e22737058729c79c61c98c5536a5063bd9467675d"},"link":{"materials":[{"uri":"text.txt","hash":"616e953d8784d4e15a17055a91ac7539bca32350850ac5157efffdda6a719a7b"}],"stepName":"build","products":[{"uri":"text.txt","hash":"616e953d8784d4e15a17055a91ac7539bca32350850ac5157efffdda6a719a7b"}]}}
    And header Content-Type = 'application/json'
    When method POST
    Then status 204

  Scenario: store link with invalid specifications should return a 400 error
    Given path '/api/link/supplychainid'
    And request {"signature":{"keyId":"keyId1","signature":"sig1"},"link":{"command":["command1","command2"],"materials":[{"uri":"materialsUri1","hash":"materialsHash1"},{"uri":"materialsUri2","hash":"materialsHash2"}],"stepName":"step name","products":[{"uri":"productsUri1","hash":"productsHash1"},{"uri":"productsUri12","hash":"productsHash2"}]}}
    And header Content-Type = 'application/json'
    When method POST
    Then status 400