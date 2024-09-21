const backgrounds = [
  {
    min: 5,
    max: 6,
    src: "0.jpg"
  },
  {
    min: 6,
    max: 7,
    src: "1.jpg"
  },
  {
    min: 7,
    max: 8,
    src: "2.jpg"
  },
  {
    min: 8,
    max: 9,
    src: "3.jpg"
  },
  {
    min: 9,
    max: 10,
    src: "4.jpg"
  },
  {
    min: 10,
    max: 12,
    src: "5.jpg"
  },
  {
    min: 12,
    max: 14,
    src: "6.jpg"
  },
  {
    min: 14,
    max: 15,
    src: "7.jpg"
  },
  {
    min: 15,
    max: 16,
    src: "8.jpg"
  },
  {
    min: 16,
    max: 17,
    src: "9.jpg"
  },
  {
    min: 17,
    max: 18,
    src: "10.jpg"
  },
  {
    min: 18,
    max: 19,
    src: "11.jpg"
  },
  {
    min: 19,
    max: 20,
    src: "12.jpg"
  },
  {
    min: 20,
    max: 23,
    src: "13.jpg"
  },
  {
    min: 23,
    max: 2,
    src: "14.jpg"
  },
  {
    min: 2,
    max: 4,
    src: "15.jpg"
  },
];

const backgroundEl = document.getElementById("background");

const getCurrentHour = () => {
  const currentDate = new Date();
  return currentDate.getHours();
};

const setBackground = () => {
  const currentHour = getCurrentHour();
  const currentBackground = backgrounds.find((background) =>
    background.min <= currentHour && background.max > currentHour
  );
  if (currentBackground == null) return;
  backgroundEl.style.backgroundImage = `url(./background/${currentBackground.src})`;
};

window.addEventListener("load", () => {
  setBackground();

  const everyHour = 1000 * 60 * 60;
  setInterval(setBackground, everyHour);
});
