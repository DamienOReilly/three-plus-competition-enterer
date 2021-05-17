package org.damienoreilly.tpce

object TestData {

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
    |   "access_token": "dummytoken",
    |   "token_type": "bearer",
    |   "expires_in": 2591999,
    |   "scope": "read write",
    |   "jti": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
    |}
  """.stripMargin

  val loginFailureResponse: String =
    """
      |{
      |   "error": "invalid_grant",
      |   "error_description": "Bad credentials"
      |}
    """.stripMargin

  val competitionEnteredResponse: String =
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

  val remainingIsZeroCompetitions =
    """
    |[{
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
    |}]
    |""".stripMargin

  val staticCompetition =
    """
    |[{
    |	"id": 555,
    |	"category": "competitions",
    |	"categorySecondary": "",
    |	"title": "Click here to view ....",
    |	"subtitle": "Blah blah",
    |	"type": "static",
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
    |	"help": "",
    |	"name": "[COMPETITION] Click here to view...",
    |	"supplierName": "Competition",
    |	"qrCode": false,
    |	"urlName": "some-url",
    |	"maxNbOfOffer": 1,
    |	"remaining": 0
    |}]
    |""".stripMargin

  val enterCompetitionErrorResponse: String =
    """
    |{
    |  "message" : "subscriber.offer.limit.reached",
    |  "status" : 411
    |}
  """.stripMargin

}
