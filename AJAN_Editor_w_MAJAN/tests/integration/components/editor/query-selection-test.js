import {module, test} from "qunit";
import hbs from "htmlbars-inline-precompile";
import {render} from "@ember/test-helpers";
import {setupRenderingTest} from "ember-qunit";

module("Integration | Component | editor/query-selection", function(hooks) {
	setupRenderingTest(hooks);

	test("it renders", async function(assert) {
		// Set any properties with this.set('myProperty', 'value');
		// Handle any actions with this.set('myAction', function(val) { ... });

		await render(hbs`{{editor/query-selection}}`);

		assert.equal(this.element.textContent.trim(), "");

		// Template block usage:
		await render(hbs`
      {{#editor/query-selection}}
        template block text
      {{/editor/query-selection}}
    `);

		assert.equal(this.element.textContent.trim(), "template block text");
	});
});
