/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.contentmodel;

/**
 * Interface for items which have multimedia
 *
 * @author bjornl
 */
public interface HasMultimedia {
    /**
     * Get the multimedia.
     *
     * @return the multimedia.
     */
    public Multimedia getMultimedia();

    /**
     * Set the multimedia
     *
     * @param multimedia
     */
    public void setMultimedia(Multimedia multimedia);
}
