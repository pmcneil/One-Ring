/* Copyright 2010, 2011 Peter McNeil

This file is part of One Ring.

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy
of the License at http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
package com.nerderg.rules

class RuleSetController {

    def ruleSetService

    def index = {
        redirect(action: "list", params: params)
    }

    /**
     * list the currently loaded rulesets
     */
    def list = {
        List ruleSets = ruleSetService.getRuleSetNames().sort { a, b -> a <=> b}
        params.max = Math.min(params.max ? params.long('max') : 10l, 100l)
        params.offset = params.offset ? params.long('offset') : 0l
        List subList = limit(ruleSets, params.max, params.offset)
        [ruleSetList: subList, ruleSetTotal: ruleSets.size()]
    }

    /**
     * read the rulesets from the file system pre process and store in memory. If any ruleset does not pass the tests the entire update is abandoned.
     */
    def update = {
        try {
            ruleSetService.update()
            flash.message = "Ruleset updated"
            redirect(action: 'list')
        } catch (e) {
            flash.message = "Ruleset update failed. Rules have <b>not</b> been updated."
            flash.error = e.message
            redirect(action: 'list')
        }
    }

    /**
     * This does an SQL style offest limit on a list, returning the remaining elements in a list after the <em>offset</em> up to the <em>limit</em> of elements
     * if the offset is > the number of elements in the list and empty list is returned
     * todo move to a utility class
     * @param list the list that you need a subset of
     * @param limit the maximum number of elements to return
     * @param offset offset into the list
     * @return a list with a maximum of limit elements offset into the list.
     */
    private List limit(List list, long limit, long offset) {
        if(limit == 0) {
            throw new IllegalArgumentException("Limit has to be greater than zero")
        }

        long maxIndex = Math.max((list.size() - 1), 0)
        if (maxIndex == 0) {
            return list
        }

        if (offset > maxIndex) {
            return []
        }

        long top = Math.min((offset + (limit - 1)), maxIndex)

        log.debug "offset $offset, top $top"
        if (top == 0) {
            return list
        }

        return list[offset..top]
    }
}
