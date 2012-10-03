/*
 * 	libSickBeard is a java library for communication with sickbeard
 * 	Copyright (C) 2012  David Stocking dmstocking@gmail.com
 * 
 * 	http://code.google.com/p/sick-stashe/
 * 	
 * 	libSickBeard is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sickbeard.json.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.sickbeard.json.type.JsonBoolean;

import java.lang.reflect.Type;

public class JsonBooleanDeserializer implements JsonDeserializer<JsonBoolean> {

	@Override
	public JsonBoolean deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
		try {
            String value = arg0.getAsJsonPrimitive().getAsString();
            if ( value.toLowerCase().equals("true") ) {
            	return new JsonBoolean(true);
            } else if ( value.toLowerCase().equals("false") ) {
            	return new JsonBoolean(false);
            } else {
            	return new JsonBoolean(Integer.valueOf(value) != 0);
            }
        } catch (ClassCastException e) {
            throw new JsonParseException("Cannot parse JsonBoolean string '" + arg0.toString() + "'", e);
        } catch (Exception e) {
            throw new JsonParseException("Cannot parse JsonBoolean string '" + arg0.toString() + "'", e);
        }
	}
}
