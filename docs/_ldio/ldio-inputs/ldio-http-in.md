---
layout: default
parent: LDIO Inputs
title: HTTP In
---

# LDIO HTTP In

***Ldio:HttpIn***

The LDIO Http In is a basic Http Listener. 

Data can be written to ``http://{hostname}:{port}/{pipeline name}``

## Config

This component has no required config

## Pausing

When paused, this component will return an 503 response to any HTTP-calls it receives