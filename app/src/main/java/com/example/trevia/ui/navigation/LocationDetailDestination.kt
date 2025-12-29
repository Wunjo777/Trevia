package com.example.trevia.ui.navigation

object LocationDetailDestination {
    // 定义地点详情页的路由
    const val ROUTE = "location_detail"

    // 定义需要传递的参数名称
    const val POI_ID_ARG = "poiId"
    const val LOCATION_NAME_ARG = "locationName"
    const val LOCATION_ADDRESS_ARG = "locationAddress"
    const val LOCATION_LATITUDE_ARG = "latitude"
    const val LOCATION_LONGITUDE_ARG = "longitude"

    // 通过这些参数访问地点详情页
    const val routeWithArgs = "$ROUTE/{$POI_ID_ARG}/{$LOCATION_NAME_ARG}/{$LOCATION_ADDRESS_ARG}/{$LOCATION_LATITUDE_ARG}/{$LOCATION_LONGITUDE_ARG}"
}

