$(function() {
    if ($('#editor').length == 1) {
        var editor = ace.edit("editor");
        var GroovyMode = require("ace/mode/groovy").Mode;
        editor.getSession().setMode(new GroovyMode());
        $('#ruleSet').hide();
        $('#save').click(function(event) {
            var val = editor.getSession().getValue();
            $('#editor').hide();
            $('#ruleSet').show().val(val);
        });
    }
});