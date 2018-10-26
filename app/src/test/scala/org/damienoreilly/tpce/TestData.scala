package org.damienoreilly.tpce

object TestData {

  val badCredentialsResponse: String =
    """
      |{  
      |   "error": "invalid_grant",
      |   "error_description": "Bad credentials"
      |}
    """.stripMargin

  val fatalErrorResponse: String =
    """
    |{
    |  "timestamp": 1539970430277,
    |  "status": 405,
    |  "error": "Method Not Allowed",
    |  "exception": "java.lang.Exception",
    |  "message": "Something bad happened",
    |  "path": "/some/path"
    |}
  """.stripMargin

  val loginSuccessResponse: String =
    """
    |{  
    |   "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9eyJhdWQiOlsib2F1dGgyLXJlc291cmNlIl0sInVzZXJfbmFtZSI6IjA4MzAwMDAwMDAiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiZXhwIjoxNTQzMDEwMDAwMCwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sImp0aSI6Inh4eHh4eHh4LXh4eHgteHh4eC14eHh4LXh4eHh4eHh4eHh4eCIsImNsaWVudF9pZCI6ImNsaWVudGlkIn0CYAdUF1kwzIlMbVhFeCxgWVJUbB9eEA==",
    |   "token_type": "bearer",
    |   "expires_in": 2591999,
    |   "scope": "read write",
    |   "jti": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
    |}
  """.stripMargin

  val enteredCompetitionResponse: String =
    """
    |{
    |  "voucher" : null,
    |  "expirationDate" : null,
    |  "partnerName" : null,
    |  "purchaseDate" : null,
    |  "voucherRedeemdate" : null
    |}
  """.stripMargin

  val competitionsResponse: String =
    """
    |[{
    |	"id": 111,
    |	"category": "competitions",
    |	"categorySecondary": "",
    |	"title": "Win a €250 Voucher",
    |	"subtitle": "thanks to someone.",
    |	"type": "classic",
    |	"index": 100,
    |	"order": 2,
    |	"urlBannerImageLarge": "someurl",
    |	"urlBannerImageLargeApp": "someurl",
    |	"urlBannerImageMedium": "someurl",
    |	"urlBannerImageMediumApp": "someurl",
    |	"urlBannerImageSmall": "someurl",
    |	"urlBannerImageSmallApp": "someurl",
    |	"redirectionUrl": "",
    |	"distance": null,
    |	"help": "Thanks for entering! You’re in the pot to win a €250 voucher.",
    |	"name": "[COMPETITION] Win a €250 Voucher",
    |	"supplierName": "Competition",
    |	"qrCode": false,
    |	"urlName": "some-url",
    |	"maxNbOfOffer": 1,
    |	"remaining": 1
    |}, {
    |	"id": 222,
    |	"category": "competitions",
    |	"categorySecondary": "",
    |	"title": "Win a €250 Voucher",
    |	"subtitle": "thanks to someone.",
    |	"type": "classic",
    |	"index": 100,
    |	"order": 2,
    |	"urlBannerImageLarge": "someurl",
    |	"urlBannerImageLargeApp": "someurl",
    |	"urlBannerImageMedium": "someurl",
    |	"urlBannerImageMediumApp": "someurl",
    |	"urlBannerImageSmall": "someurl",
    |	"urlBannerImageSmallApp": "someurl",
    |	"redirectionUrl": "",
    |	"distance": null,
    |	"help": "Thanks for entering! You’re in the pot to win a €500 voucher.",
    |	"name": "[COMPETITION] Win a €500 Voucher",
    |	"supplierName": "Competition",
    |	"qrCode": false,
    |	"urlName": "some-url",
    |	"maxNbOfOffer": 1,
    |	"remaining": 1
    |}, {
    |	"id": 333,
    |	"category": "competitions",
    |	"categorySecondary": "",
    |	"title": "Win a €750 Voucher",
    |	"subtitle": "thanks to someone.",
    |	"type": "classic",
    |	"index": 100,
    |	"order": 2,
    |	"urlBannerImageLarge": "someurl",
    |	"urlBannerImageLargeApp": "someurl",
    |	"urlBannerImageMedium": "someurl",
    |	"urlBannerImageMediumApp": "someurl",
    |	"urlBannerImageSmall": "someurl",
    |	"urlBannerImageSmallApp": "someurl",
    |	"redirectionUrl": "",
    |	"distance": null,
    |	"help": "Thanks for entering! You’re in the pot to win a €750 voucher.",
    |	"name": "[COMPETITION] Win a €750 Voucher",
    |	"supplierName": "Competition",
    |	"qrCode": false,
    |	"urlName": "some-url",
    |	"maxNbOfOffer": 1,
    |	"remaining": 0
    |}, {
    |	"id": 444,
    |	"category": "competitions",
    |	"categorySecondary": "",
    |	"title": "Win a €1000 Voucher",
    |	"subtitle": "thanks to someone.",
    |	"type": "classic",
    |	"index": 100,
    |	"order": 2,
    |	"urlBannerImageLarge": "someurl",
    |	"urlBannerImageLargeApp": "someurl",
    |	"urlBannerImageMedium": "someurl",
    |	"urlBannerImageMediumApp": "someurl",
    |	"urlBannerImageSmall": "someurl",
    |	"urlBannerImageSmallApp": "someurl",
    |	"redirectionUrl": "",
    |	"distance": null,
    |	"help": "Thanks for entering! You’re in the pot to win a €1000 voucher.",
    |	"name": "[COMPETITION] Win a €1000 Voucher",
    |	"supplierName": "Competition",
    |	"qrCode": false,
    |	"urlName": "some-url",
    |	"maxNbOfOffer": 1,
    |	"remaining": 1
    |}]
  """.stripMargin

  val enterCompetitionErrorResponse: String =
    """
    |{
    |  "message" : "subscriber.offer.limit.reached",
    |  "status" : 411
    |}
  """.stripMargin

}