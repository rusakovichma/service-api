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

package com.epam.ta.reportportal.ws.converter.builders;

import com.epam.ta.reportportal.entity.filter.UserFilter;
import com.epam.ta.reportportal.entity.project.Project;
import com.epam.ta.reportportal.entity.widget.Widget;
import com.epam.ta.reportportal.ws.model.widget.WidgetPreviewRQ;
import com.epam.ta.reportportal.ws.model.widget.WidgetRQ;
import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Pavel Bortnik
 */
public class WidgetBuilder implements Supplier<Widget> {

	private Widget widget;

	public WidgetBuilder() {
		widget = new Widget();
	}

	public WidgetBuilder(Widget widget) {
		this.widget = widget;
	}

	public WidgetBuilder addWidgetRq(WidgetRQ widgetRQ) {
		widget.setName(widgetRQ.getName());

		widget.getWidgetOptions().clear();
		widget.getWidgetOptions().putAll(widgetRQ.getContentParameters().getWidgetOptions());

		widget.setWidgetType(widgetRQ.getContentParameters().getWidgetType());
		widget.setItemsCount(widgetRQ.getContentParameters().getItemsCount());

		widget.getContentFields().clear();
		widget.getContentFields()
				.addAll(Optional.ofNullable(widgetRQ.getContentParameters().getContentFields()).orElse(Collections.emptyList()));
		return this;
	}

	public WidgetBuilder addWidgetPreviewRq(WidgetPreviewRQ previewRQ) {
		widget.getWidgetOptions().clear();
		widget.getWidgetOptions().putAll(previewRQ.getContentParameters().getWidgetOptions());

		widget.setWidgetType(previewRQ.getContentParameters().getWidgetType());
		widget.setItemsCount(previewRQ.getContentParameters().getItemsCount());

		widget.getContentFields().clear();
		widget.getContentFields()
				.addAll(Optional.ofNullable(previewRQ.getContentParameters().getContentFields()).orElse(Collections.emptyList()));
		return this;
	}

	public WidgetBuilder addProject(Long projectId) {
		Project project = new Project();
		project.setId(projectId);
		widget.setProject(project);
		return this;
	}

	public WidgetBuilder addFilters(Iterable<UserFilter> userFilters) {
		Optional.ofNullable(userFilters).ifPresent(it -> widget.setFilters(Sets.newHashSet(it)));
		return this;
	}

	@Override
	public Widget get() {
		return widget;
	}
}
