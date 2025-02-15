/*
 *  Copyright (C) 2022-2024 PolarAstrumLab
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package cc.polarastrum.aiyatsbus.core

/**
 * Aiyatsbus
 * com.mcstarrysky.aiyatsbus.core.Aiyatsbus
 *
 * @author mical
 * @since 2024/2/17 15:31
 */
object Aiyatsbus {

    const val VERSION_ID = 0

    private var api: AiyatsbusAPI? = null

    fun api(): AiyatsbusAPI {
        return api ?: error("AiyatsbusAPI has not finished loading, or failed to load!")
    }

    fun register(api: AiyatsbusAPI) {
        Aiyatsbus.api = api
    }
}