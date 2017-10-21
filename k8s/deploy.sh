#!/bin/bash
$(dirname $0)/gateway.yaml.sh | kubectl apply -f -