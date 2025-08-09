word="payouts"
first=$(printf "%s" "$word" | cut -c1 | tr '[:lower:]' '[:upper:]')
rest=$(printf "%s" "$word" | cut -c2-)
echo "$first$rest"