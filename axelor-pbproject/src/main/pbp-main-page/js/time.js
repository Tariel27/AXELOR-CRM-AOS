// Time function and display
function updateTime() {
    const timeContainer = document.querySelector(".time-container");
    if (timeContainer) {
        const now = new Date();
        const hours = now.getHours().toString().padStart(2, "0");
        const minutes = now.getMinutes().toString().padStart(2, "0");

        const hourElement = document.getElementById("hour");
        const colonElement = document.getElementById("colon");
        const minuteElement = document.getElementById("minute");

        if (hourElement && colonElement && minuteElement) {
            hourElement.textContent = hours;
            minuteElement.textContent = minutes;
        }
    }
}
setInterval(updateTime, 1000);
updateTime();

// Date function and display
function updateDate() {
    const dateContainer = document.querySelector(".date-container");

    if (dateContainer) {
        const now = new Date();
        const daysOfWeek = [
            "Воскресенье",
            "Понедельник",
            "Вторник",
            "Среда",
            "Четверг",
            "Пятница",
            "Суббота"
        ];
        const months = [
            "Январь",
            "Февраль",
            "Март",
            "Апрель",
            "Май",
            "Июнь",
            "Июль",
            "Август",
            "Сентябрь",
            "Октябрь",
            "Ноябрь",
            "Декабрь"
        ];
        const dayOfWeek = daysOfWeek[now.getDay()];
        const dayOfMonth = now.getDate();
        const month = months[now.getMonth()];
        const year = now.getFullYear();

        document.getElementById("date-alpha-day").textContent = `${dayOfWeek}`; // Alphabetical day
        document.getElementById("date-alpha-month").textContent = `${month}`; // Alphabetical month
        document.getElementById("date-num-day").textContent = `${dayOfMonth}`; // Numerical day
        document.getElementById("date-num-year").textContent = `${year}`; // Numerical year
    }
}
updateDate();

