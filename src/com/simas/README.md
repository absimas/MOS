ToDo:
- ~~Scheduler~~
- ~~Step-by-step VM execution~~
- ~~VM execution with interruptions~~
- Avoid sleeping threads in Scheduler?
  - Problem is that not all waiters get notified before the scheduler is executed thus we result in some waiters being left behind.
- ~~Privilege-requiring commands that are sent from VM->JobGovernor need to change their argument to an absolute value.~~
- ~~Increase system process priorities~~