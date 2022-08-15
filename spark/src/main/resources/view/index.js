"use strict"

window.onload = () => {
    const input = document.getElementById("input");
    const output = document.getElementById("output");
    const button = document.getElementById("execute");

    const placeholder = `Enter your code here...`;

    input.addEventListener('keydown', (event) => {
        if (event.key === 'Tab') {
            console.log("TAB")

            event.preventDefault();

            const start = input.selectionStart;
            const end = input.selectionEnd;

            input.value = input.value.substring(0, start) +
                "\t" + input.value.substring(end);

            input.selectionStart =
                input.selectionEnd = start + 1;
        }
    })

    button.addEventListener('click', (event) => {
        if (input.value === '') {
            output.value = '';
            return
        }

        let xhr = new XMLHttpRequest();

        xhr.open("POST", "executor", true);

        xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

        xhr.send("program=" + encodeURIComponent(input.value));

        xhr.onreadystatechange = function () {
            if (xhr.status === 200) {
                output.value = xhr.responseText;
            }
        };

        output.focus();
    });
};