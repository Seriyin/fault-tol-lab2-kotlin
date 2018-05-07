# Exercise in fault tolerance - Active Replication #

- Take several servers with the same single bank account.

- Expects multiple clients asking for balance or movements over TCP.

- Collect all balances of individual clients that make transactions on behalf of that account.

- Sum and collect a total balance.

- Check if summed balance matches account balance.

- Use Spread toolkit to ensure atomic multicasts.

- Each replica responds to the client.

- Client issues a group request.