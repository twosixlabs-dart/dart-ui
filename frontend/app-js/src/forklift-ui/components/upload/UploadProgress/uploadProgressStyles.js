const styles = () => ({
  wrapper: {
    paddingLeft: 20,
    paddingRight: 0,
    overflow: 'scroll',
    maxHeight: 600,
  },
  closeButton: {
    position: 'absolute',
    top: 18,
    right: 12,
    background: 'transparent',
    border: 'unseen',
    fontSize: 18,
    cursor: 'pointer',
    '&:hover': {
      opacity: 0.5,
    },
  },
});

export default styles;
