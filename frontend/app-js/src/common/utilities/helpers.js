export default function uuidv4() {
  return ([1e7] + -1e3 + -4e3 + -8e3 + -1e11)
    // eslint-disable-next-line arrow-body-style
    .replace(/[018]/g, (c) => {
      // eslint-disable-next-line no-bitwise,no-mixed-operators
      return (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16);
    });
}

export const poll = (fn, condition, complete, timeout, intervalIn) => {
  const endTime = timeout === undefined || timeout === null ? null : Number(new Date()) + timeout;
  const interval = intervalIn || 3000;

  const pollLoop = () => {
    const res = fn();
    const passes = condition(res);
    const hasTimeLeft = endTime === null ? true : new Date() < endTime;
    if (!passes || !hasTimeLeft) {
      if (complete) complete();
      return;
    }

    setTimeout(pollLoop, interval);
  };

  pollLoop();
};
