# Remotecache

## TODO
- [ ] Tests


## Design Decisions

- We don't handle authentication. We assume it's outside of the scope of the exercise. 
  If I had to handle it, I'd generate signed JWTs and pass them as bearer tokens in the gRPC headers.
- 