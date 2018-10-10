/*
 * Copyright 2018 EPAM Systems
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
package com.epam.ta.reportportal.core.events.activity;

import com.epam.ta.reportportal.entity.project.Project;

/**
 * @author Andrei Varabyeu
 */
public class DefectTypeDeletedEvent {
	private final Project before;
	private final String updatedBy;
	private final String id;

	public DefectTypeDeletedEvent(String id, Project before, String updatedBy) {
		this.before = before;
		this.updatedBy = updatedBy;
		this.id = id;
	}

	public Project getBefore() {
		return before;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public String getId() {
		return id;
	}
}
