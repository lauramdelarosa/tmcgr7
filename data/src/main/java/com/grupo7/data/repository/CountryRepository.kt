package com.grupo7.data.repository

import com.grupo7.data.source.LocationDataSource
import com.grupo7.data.source.PermissionChecker

class CountryRepository(
    private val locationDataSource: LocationDataSource,
    private val permissionChecker: PermissionChecker
) {
    companion object {
        const val DEFAULT_COUNTRY_NAME = "US"
    }

    suspend fun getCountryLocation(): String {
        return if (permissionChecker.check(PermissionChecker.Permission.COARSE_LOCATION)) {
            locationDataSource.findLastLocation() ?: DEFAULT_COUNTRY_NAME
        } else {
            DEFAULT_COUNTRY_NAME
        }
    }
}