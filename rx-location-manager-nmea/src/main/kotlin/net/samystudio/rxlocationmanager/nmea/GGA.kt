package net.samystudio.rxlocationmanager.nmea

class GGA(message: String) : Nmea(message) {
    val time: String by lazy { data[1] }
    val latitude: Double? by lazy {
        convertNmeaLocation(
            data[2],
            LocationDirection.valueOf(data[3], LocationDirection.N)
        )
    }
    val longitude: Double? by lazy {
        convertNmeaLocation(
            data[4],
            LocationDirection.valueOf(data[5], LocationDirection.E)
        )
    }
    val quality: Quality by lazy {
        try {
            return@lazy Quality.values()[data[6].toInt()]
        } catch (e: NumberFormatException) {
        } catch (e: ArrayIndexOutOfBoundsException) {
        }
        Quality.NO_FIX
    }
    val satelliteCount: Int by lazy { data[7].toIntOrNull() ?: 0 }
    val horizontalDilutionOfPrecision: Double? by lazy { data[8].toDoubleOrNull() }
    val altitude: Double? by lazy { data[9].toDoubleOrNull() }
    val ellipsoidalOffset: Double? by lazy { data[11].toDoubleOrNull() }
    val differentialGpsAge: Double? by lazy { data[13].toDoubleOrNull() }
    val differentialGpsStationId: String by lazy { data[14] }

    override fun getTokenValidators(): Array<TokenValidator> {
        val optionalDoubleValidator = DoubleValidator(true)
        val meterValidator = EnumValidator(arrayOf('M'), true)

        return arrayOf(
            // type $__GGA
            TypeValidator("GGA"),
            // UTC time hhmmss(.sss)
            TimeValidator(true),
            // latitude ddmm.ssss
            LatitudeValidator(true),
            // N or S
            EnumValidator(arrayOf('N', 'S'), true),
            // longitude ddddmm.ssss
            LongitudeValidator(true),
            // W or E
            EnumValidator(arrayOf('W', 'E'), true),
            // quality 0, 1 or 2 (not fixed, fixed, differential fixed)
            EnumValidator(arrayOf('0', '1', '2'), true),
            // satellites count 0-12
            IntValidator(true, 0, 12),
            // horizontal dilution of precision
            optionalDoubleValidator,
            // altitude geoid (mean sea level) in meter
            optionalDoubleValidator,
            // altitude unit M
            meterValidator,
            // WGS-84 earth ellipsoid offset
            optionalDoubleValidator,
            // ellipsoid offset unit M
            meterValidator,
            // age of differential GPS data (seconds)
            optionalDoubleValidator,
            // station
            StringValidator(true, 4, 4)
        )
    }

    enum class Quality {
        NO_FIX, FIX, DIFFERENTIAL_FIX;
    }
}
