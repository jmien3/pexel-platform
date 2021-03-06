// @formatter:off
/*
 * Pexel Project - Minecraft minigame server platform. 
 * Copyright (C) 2014 Matej Kormuth <http://www.matejkormuth.eu>
 * 
 * This file is part of Pexel.
 * 
 * Pexel is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * Pexel is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 */
// @formatter:on
package eu.matejkormuth.pexel.protocol.requests;

import java.nio.ByteBuffer;
import java.util.Set;

import eu.matejkormuth.pexel.commons.Providers;
import eu.matejkormuth.pexel.commons.SlaveMinecraftServerType;
import eu.matejkormuth.pexel.commons.storage.MapDescriptor;
import eu.matejkormuth.pexel.network.Request;
import eu.matejkormuth.pexel.protocol.PexelProtocol;

/**
 * Reports minigames and maps on this server. Sent by slave server after connecting to master.
 */
public class InServerMetaDataMessage extends Request {
    public Set<String>              minigames;
    public Set<MapDescriptor>       maps;
    public SlaveMinecraftServerType software;
    public String                   softwareVersion;
    public int                      slots;
    
    public InServerMetaDataMessage() {
    }
    
    public InServerMetaDataMessage(final Set<String> minigames,
            final Set<MapDescriptor> maps, final SlaveMinecraftServerType software,
            final String softwareVersion, final int slots) {
        this.minigames = minigames;
        this.maps = maps;
        this.software = software;
        this.softwareVersion = softwareVersion;
        this.slots = slots;
    }
    
    static class ServerConfiguration {
        public Set<String>              minigames;
        public Set<MapDescriptor>       maps;
        public SlaveMinecraftServerType software;
        public int                      slots;
        public String                   softwareVersion;
        
        public ServerConfiguration from(
                final InServerMetaDataMessage serverConfigurationRequest) {
            this.minigames = serverConfigurationRequest.minigames;
            this.maps = serverConfigurationRequest.maps;
            this.software = serverConfigurationRequest.software;
            this.slots = serverConfigurationRequest.slots;
            this.softwareVersion = serverConfigurationRequest.softwareVersion;
            return this;
        }
        
        public void apply(final InServerMetaDataMessage serverConfigurationRequest) {
            serverConfigurationRequest.minigames = this.minigames;
            serverConfigurationRequest.maps = this.maps;
            serverConfigurationRequest.software = this.software;
            serverConfigurationRequest.slots = this.slots;
            serverConfigurationRequest.softwareVersion = this.softwareVersion;
        }
    }
    
    @Override
    public ByteBuffer toByteBuffer() {
        String json = Providers.JSON.toJson(new ServerConfiguration().from(this));
        return ByteBuffer.allocate(json.length()).put(
                json.getBytes(PexelProtocol.CHARSET));
    }
    
    @Override
    public void fromByteBuffer(final ByteBuffer buffer) {
        String json = new String(buffer.array(), PexelProtocol.CHARSET);
        ServerConfiguration conf = Providers.JSON.fromJson(json,
                ServerConfiguration.class);
        conf.apply(this);
    }
}
