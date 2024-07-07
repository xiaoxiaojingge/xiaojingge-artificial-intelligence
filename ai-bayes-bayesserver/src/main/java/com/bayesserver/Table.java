//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.bayesserver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CancellationException;

public final class Table implements Distribution {
    private VariableContextCollection a;
    private int b;
    private double[] c;
    private int[] d;
    private int[] e;
    private boolean f;
    private Node g;
    private boolean h;
    private Distribution i;

    public Table(Table table, boolean copyValues) {
        this(table, copyValues, (Integer) null);
    }

    public Table(Table table, boolean copyValues, Integer timeShift) {
        if (table == null) {
            throw new NullPointerException("table");
        } else {
            this.a = new VariableContextCollection(table.a, timeShift);
            this.b = table.b;
            this.c = new double[table.c.length];
            this.e = new int[table.e.length];
            System.arraycopy(table.e, 0, this.e, 0, table.e.length);
            this.d = new int[table.d.length];
            System.arraycopy(table.d, 0, this.d, 0, table.d.length);
            this.f = table.f;
            if (copyValues) {
                System.arraycopy(table.c, 0, this.c, 0, this.c.length);
            }

        }
    }

    public final void nonZero(NonZeroValues values) {
        if (values == null) {
            throw new NullPointerException();
        } else {
            for (int var2 = 0; var2 < this.b; ++var2) {
                double var3;
                if ((var3 = this.c[var2]) != 0.0) {
                    values.value(var2, var3);
                }
            }

        }
    }

    public final MaxValue getMaxValue() {
        int var1 = 0;
        double var2 = this.c[0];

        for (int var4 = 1; var4 < this.c.length; ++var4) {
            double var5;
            if ((var5 = this.c[var4]) > var2) {
                var2 = var5;
                var1 = var4;
            }
        }

        return new MaxValue(var2, var1);
    }

    public Table(Variable variable) {
        if (variable == null) {
            throw new NullPointerException("variable");
        } else {
            this.a(new VariableContext[]{new VariableContext(variable, (Integer) null, HeadTail.HEAD)});
        }
    }

    public Table(VariableContext variableContext) {
        if (variableContext == null) {
            throw new NullPointerException("variableContext");
        } else {
            this.a(new VariableContext[]{new VariableContext(variableContext)});
        }
    }

    public Table(List<Variable> variables, Integer time) {
        this(variables, time, HeadTail.HEAD);
    }

    public Table(List<Variable> variables, Integer time, HeadTail headTail) {
        if (variables == null) {
            throw new NullPointerException("variables");
        } else {
            VariableContext[] var4 = new VariableContext[variables.size()];

            for (int var5 = 0; var5 < var4.length; ++var5) {
                var4[var5] = new VariableContext((Variable) variables.get(var5), (Integer) null, headTail);
            }

            this.a(var4);
        }
    }

    public Table(VariableContextCollection variableContexts) {
        if (variableContexts == null) {
            throw new NullPointerException("variableContexts");
        } else {
            VariableContext[] var2 = new VariableContext[variableContexts.size()];

            for (int var3 = 0; var3 < var2.length; ++var3) {
                var2[var3] = new VariableContext(variableContexts.get(var3));
            }

            this.a(var2);
        }
    }

    Table(VariableContext[] variableContexts, boolean copy) {
        this.a(variableContexts);
    }

    public Table(VariableContext[] variableContexts) {
        if (variableContexts == null) {
            throw new NullPointerException("variableContexts");
        } else {
            int var2;
            VariableContext[] var3 = new VariableContext[var2 = variableContexts.length];

            for (int var4 = 0; var4 < var2; ++var4) {
                var3[var4] = new VariableContext(variableContexts[var4]);
            }

            this.a(var3);
        }
    }

    public Table(List<VariableContext> variableContexts) {
        if (variableContexts == null) {
            throw new NullPointerException("variableContexts");
        } else {
            int var2;
            VariableContext[] var3 = new VariableContext[var2 = variableContexts.size()];

            for (int var4 = 0; var4 < var2; ++var4) {
                var3[var4] = new VariableContext((VariableContext) variableContexts.get(var4));
            }

            this.a(var3);
        }
    }

    public Table(List<VariableContext> variableContexts, HeadTail headTail) {
        if (variableContexts == null) {
            throw new NullPointerException("variableContexts");
        } else {
            int var3;
            VariableContext[] var4 = new VariableContext[var3 = variableContexts.size()];

            for (int var5 = 0; var5 < var3; ++var5) {
                VariableContext var6 = (VariableContext) variableContexts.get(var5);
                var4[var5] = new VariableContext(var6.getVariable(), var6.getTime(), headTail);
            }

            this.a(var4);
        }
    }

    public final void timeShift(int units) {
        if (this.i != null) {
            this.i.timeShift(units);
        } else {
            this.b();
            this.a.a(units);

            for (int var2 = 0; var2 < this.a.size(); ++var2) {
                VariableContext var3;
                if ((var3 = this.a.get(var2)).getTime() != null) {
                    var3.a(var3.getTime() + units);
                }
            }

        }
    }

    public final boolean getLocked() {
        return this.h;
    }

    public final void setLocked(boolean value) {
        this.h = value;
    }

    public final boolean isReadOnly() {
        if (this.g != null) {
            return true;
        } else {
            return this.h;
        }
    }

    public final void randomize(RandomNumberGenerator random) {
        this.b();
        ArrayList var2 = new ArrayList();
        ArrayList var3 = new ArrayList();

        int var4;
        for (var4 = 0; var4 < this.a.size(); ++var4) {
            VariableContext var5;
            if ((var5 = this.a.get(var4)).isHead()) {
                var3.add(var5);
            } else {
                var2.add(var5);
            }
        }

        var4 = 1;

        for (int var14 = 0; var14 < var3.size(); ++var14) {
            VariableContext var6;
            Variable var7 = (var6 = (VariableContext) var3.get(var14)).getVariable();
            var6.getTime();
            if (var7.getStates().size() <= 0) {
                throw new IllegalArgumentException("A variable has less than 1 state.");
            }

            var4 *= var7.getStates().size();
        }

        var2.addAll(var3);
        TableIterator var15 = new TableIterator(this, var2);
        TableIterator var16 = new TableIterator(this, var2);
        int var17 = this.b / var4;

        for (int var8 = 0; var8 < var17; ++var8) {
            double var9 = 0.0;
            int var11 = 0;

            while (var11 < var4) {
                double var12;
                for (var12 = random.nextDouble(); var12 == 0.0; var12 = random.nextDouble()) {
                }

                var15.setValue(var12);
                var9 += var12;
                ++var11;
                var15.increment();
            }

            var11 = 0;

            while (var11 < var4) {
                var16.setValue(var16.getValue() / var9);
                ++var11;
                var16.increment();
            }
        }

    }

    private boolean a() {
        double var1;
        if ((var1 = this.sum()) == 0.0) {
            return false;
        } else if (var1 == 1.0) {
            return true;
        } else {
            int var3 = this.c.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                double[] var10000 = this.c;
                var10000[var4] /= var1;
            }

            return true;
        }
    }

    public final boolean normalize(boolean unifyZeroSum) {
        this.b();
        ArrayList var2 = new ArrayList();
        ArrayList var3 = new ArrayList();

        int var4;
        for (var4 = 0; var4 < this.a.size(); ++var4) {
            VariableContext var5;
            if ((var5 = this.a.get(var4)).isHead()) {
                var3.add(var5);
            } else {
                var2.add(var5);
            }
        }

        if (var2.size() == 0) {
            boolean var17;
            if (!(var17 = this.a()) && unifyZeroSum) {
                double var19 = 1.0 / (double) this.b;

                for (int var20 = 0; var20 < this.b; ++var20) {
                    this.set(var20, var19);
                }
            }

            return var17;
        } else if (var2.size() == this.a.size()) {
            this.setAll(1.0);
            return true;
        } else {
            da var18 = new da(0);
            List var6 = a(var2, var3, var18);
            var4 = (Integer) var18.a;
            TableIterator var7 = new TableIterator(this, var6);
            TableIterator var8 = new TableIterator(this, var6);
            int var9 = this.b / var4;
            boolean var10 = true;
            double var11 = 1.0 / (double) var4;

            for (int var13 = 0; var13 < var9; ++var13) {
                double var14 = 0.0;
                int var16 = 0;

                while (var16 < var4) {
                    var14 += var7.getValue();
                    ++var16;
                    var7.increment();
                }

                if (var14 == 0.0) {
                    var10 = false;
                }

                var16 = 0;

                while (var16 < var4) {
                    if (var14 != 0.0) {
                        var8.setValue(var8.getValue() / var14);
                    } else if (unifyZeroSum) {
                        var8.setValue(var11);
                    }

                    ++var16;
                    var8.increment();
                }
            }

            return var10;
        }
    }

    private static List<VariableContext> a(List<VariableContext> var0, List<VariableContext> var1, da<Integer> var2) {
        int var3 = (Integer) var2.a;

        ArrayList var11;
        try {
            ArrayList var4;
            (var4 = new ArrayList(var0.size() + var1.size())).addAll(var0);
            var3 = 1;

            for (int var5 = 0; var5 < var1.size(); ++var5) {
                VariableContext var6;
                Variable var7;
                if ((var7 = (var6 = (VariableContext) var1.get(var5)).getVariable()).getStates().size() <= 0) {
                    throw new IllegalArgumentException("A variable has less than 1 state.");
                }

                var3 *= var7.getStates().size();
                var4.add(var6);
            }

            var11 = var4;
        } finally {
            var2.a = var3;
        }

        return var11;
    }

    public final boolean normalize() {
        return this.normalize(false);
    }

    public final String toString() {
        return m.a(this, false);
    }

    public Table(Variable... variables) {
        if (variables == null) {
            throw new NullPointerException("variables");
        } else {
            VariableContext[] var2 = new VariableContext[variables.length];

            for (int var3 = 0; var3 < var2.length; ++var3) {
                Variable var4;
                if ((var4 = variables[var3]) == null) {
                    throw new IllegalArgumentException("Variables cannot be null");
                }

                var2[var3] = new VariableContext(var4);
            }

            this.a(var2);
        }
    }

    public Table(VariableContext[] buffer, int count) {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        } else if (count < 0) {
            throw new IllegalArgumentException("count: Value must be non negative.");
        } else if (count > buffer.length) {
            throw new IllegalArgumentException("count: Value cannot be greater than the length of buffer");
        } else {
            VariableContext[] var3 = new VariableContext[count];

            for (int var4 = 0; var4 < count; ++var4) {
                VariableContext var5;
                if ((var5 = buffer[var4]) == null) {
                    throw new IllegalArgumentException("Items cannot be null");
                }

                var3[var4] = new VariableContext(var5);
            }

            this.a(var3);
        }
    }

    public Table(VariableContext[] buffer, int count, HeadTail headTail) {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        } else if (count < 0) {
            throw new IllegalArgumentException("count: Value must be non negative.");
        } else if (count > buffer.length) {
            throw new IllegalArgumentException("count: Value cannot be greater than the length of buffer");
        } else {
            VariableContext[] var4 = new VariableContext[count];

            for (int var5 = 0; var5 < count; ++var5) {
                VariableContext var6;
                if ((var6 = buffer[var5]) == null) {
                    throw new IllegalArgumentException("Items cannot be null");
                }

                var4[var5] = new VariableContext(var6.getVariable(), var6.getTime(), headTail);
            }

            this.a(var4);
        }
    }

    public Table(Node node, Integer time) {
        if (node == null) {
            throw new NullPointerException("node");
        } else if (node.getVariables().size() != 1) {
            throw new IllegalArgumentException("This version of the constructor only accepts single variable nodes.  See other versions");
        } else {
            this.a(new VariableContext[]{new VariableContext(node.getVariables().get(0), time, HeadTail.HEAD)});
        }
    }

    public Table(Variable variable, Integer time) {
        if (variable == null) {
            throw new NullPointerException("variable");
        } else {
            this.a(new VariableContext[]{new VariableContext(variable, time, HeadTail.HEAD)});
        }
    }

    public Table(Node... nodes) {
        this(nodes, HeadTail.HEAD);
    }

    public Table(Node[] nodes, HeadTail headTail) {
        if (nodes == null) {
            throw new NullPointerException("nodes");
        } else {
            int var3 = nodes.length;
            ArrayList var4 = new ArrayList(var3);

            for (int var5 = 0; var5 < var3; ++var5) {
                Node var6;
                int var7 = (var6 = nodes[var5]).getVariables().size();

                for (int var8 = 0; var8 < var7; ++var8) {
                    var4.add(new VariableContext(var6.getVariables().get(var8), (Integer) null, headTail));
                }
            }

            this.a((VariableContext[]) var4.toArray(new VariableContext[var4.size()]));
        }
    }

    private void a(VariableContext[] var1) {
        fl.a(var1);
        int var2 = var1.length;
        this.a = new VariableContextCollection(var1, false);
        long var3 = 1L;
        this.d = new int[var2];
        this.e = new int[var2];
        this.f = true;

        for (int var5 = var2 - 1; var5 >= 0; --var5) {
            VariableContext var6;
            Variable var7 = (var6 = var1[var5]).getVariable();
            Integer var8 = var6.getTime();
            if (var7.getValueType() != VariableValueType.DISCRETE) {
                throw new IllegalArgumentException("All table variables must be discrete.");
            }

            if (var7.getKind() == VariableKind.FUNCTION) {
                throw new IllegalArgumentException("Function variable [" + var7.getName() + "] cannot be included in a distribution.");
            }

            if (var5 > 0 && var7.compareTo(var1[var5 - 1].getVariable()) == 0) {
                label54:
                {
                    if (var8 == null) {
                        if (var1[var5 - 1].getTime() != null) {
                            break label54;
                        }
                    } else if (!var8.equals(var1[var5 - 1].getTime())) {
                        break label54;
                    }

                    throw new IllegalArgumentException("Duplicate variable detected.");
                }
            }

            this.d[var5] = (int) var3;
            int var9;
            if ((var9 = var7.getStates().size()) == 0) {
                throw new IllegalArgumentException("One or more discrete variables have zero states.");
            }

            if (var9 != 2) {
                this.f = false;
            }

            if ((var3 *= (long) var9) > 2147483647L) {
                throw new UnsupportedOperationException("Tables larger than the maximum value of an Integer are not currently supported.");
            }

            this.e[var5] = var9;
        }

        this.b = (int) var3;
        this.c = new double[this.b];
    }

    public Table(Table table) {
        this(table, true, (Integer) null);
    }

    public Table(Table table, Integer timeShift) {
        this(table, true, timeShift);
    }

    public Table(Node node) {
        if (node == null) {
            throw new NullPointerException("node");
        } else {
            VariableContext[] var2 = new VariableContext[node.getVariables().size()];

            for (int var3 = 0; var3 < var2.length; ++var3) {
                var2[var3] = new VariableContext(node.getVariables().get(var3));
            }

            this.a(var2);
        }
    }

    public final Distribution getOuter() {
        return this.i;
    }

    final void a(Distribution var1) {
        this.i = var1;
    }

    public final Distribution copy() {
        return new Table(this);
    }

    public final Distribution copy(Integer timeShift) {
        return new Table(this, timeShift);
    }

    public final Node getOwner() {
        return this.g;
    }

    final void a(Node var1) {
        this.g = var1;
    }

    public final boolean areAllValuesNonZero() {
        int var1 = this.c.length;

        for (int var2 = 0; var2 < var1; ++var2) {
            if (this.c[var2] == 0.0) {
                return false;
            }
        }

        return true;
    }

    private static au a(boolean var0, int[] var1, int[] var2, bb var3, PropagationMethod var4) {
        if (var0 && var1.length == var2.length - 1 && var1[0] == 1) {
            return new dj(var1, var2);
        } else if (var3.a() > 0) {
            Object var5;
            if (var4 == PropagationMethod.MAX) {
                var5 = new ey(var3, var2);
            } else {
                var5 = new en(var3, var2);
            }

            return (au) var5;
        } else {
            throw new IllegalStateException();
        }
    }

    private void b() {
        if (this.g != null) {
            throw new IllegalStateException(String.format("Distribution cannot be changed while assigned to node [%s].", this.g.getName()));
        } else if (this.h) {
            throw new IllegalStateException("The distribution is currently locked and therefore cannot be modified.");
        }
    }

    public final void copyFrom(double[] data) {
        if (data == null) {
            throw new NullPointerException("data");
        } else {
            this.b();
            int var2 = Math.min(this.b, data.length);

            for (int var3 = 0; var3 < var2; ++var3) {
                this.c[var3] = data[var3];
            }

        }
    }

    public final void add(Table source) {
        if (source == null) {
            throw new NullPointerException("source");
        } else if (source.size() != this.size()) {
            throw new IllegalArgumentException("table counts do not match");
        } else {
            this.b();
            double[] var2 = source.c;

            for (int var3 = 0; var3 < this.b; ++var3) {
                double[] var10000 = this.c;
                var10000[var3] += var2[var3];
            }

        }
    }

    public final void copyTo(Table destination) {
        if (destination == null) {
            throw new NullPointerException("destination");
        } else {
            destination.b();
            if (destination.c.length < this.c.length) {
                throw new IllegalArgumentException("destination data is too short");
            } else {
                System.arraycopy(this.c, 0, destination.c, 0, this.c.length);
            }
        }
    }

    public final void copyTo(double[] destination) {
        if (destination == null) {
            throw new NullPointerException("destination");
        } else if (destination.length < this.c.length) {
            throw new IllegalArgumentException("destination is too short");
        } else {
            int var2 = this.c.length;

            for (int var3 = 0; var3 < var2; ++var3) {
                destination[var3] = this.c[var3];
            }

        }
    }

    public final void addAll(double value) {
        for (int var3 = 0; var3 < this.c.length; ++var3) {
            double[] var10000 = this.c;
            var10000[var3] += value;
        }

    }

    public final void setAll(double value) {
        this.b();
        int var3 = this.c.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            this.c[var4] = value;
        }

    }

    public final int stateRepeat(int index) {
        return this.d[index];
    }

    public final int stateCount(int index) {
        return this.e[index];
    }

    public final int size() {
        return this.b;
    }

    public final double get(State... states) {
        return this.c[this.getSortedIndex(states)];
    }

    public final void set(double value, State... states) {
        this.c[this.getSortedIndex(states)] = value;
    }

    public final double get(StateContext... states) {
        return this.c[this.getSortedIndex(states)];
    }

    public final void set(double value, StateContext... states) {
        this.c[this.getSortedIndex(states)] = value;
    }

    public final int getSortedIndex(State... states) {
        if (states.length != this.a.size()) {
            throw new IllegalArgumentException("The number of states does not match the number of variables in the table");
        } else {
            int var2 = 0;

            for (int var3 = 0; var3 < states.length; ++var3) {
                State var4;
                if ((var4 = states[var3]) == null) {
                    throw new IllegalArgumentException("States cannot be null");
                }

                if (var4.getVariable() == null) {
                    throw new IllegalArgumentException("One or more states do not belong to variables.");
                }

                int var5;
                if ((var5 = this.a.indexOf(var4.getVariable())) < 0) {
                    throw new IllegalStateException(String.format("State [%s] belonging to variable [%s] not found in distribution", var4.getName(), var4.getVariable().getName()));
                }

                var2 += this.d[var5] * var4.getIndex();
            }

            return var2;
        }
    }

    public final int getSortedIndex(StateContext... stateContexts) {
        if (stateContexts.length != this.a.size()) {
            throw new IllegalArgumentException("The number of states does not match the number of variables in the table");
        } else {
            int var2 = 0;

            for (int var3 = 0; var3 < stateContexts.length; ++var3) {
                StateContext var4;
                State var5;
                if ((var5 = (var4 = stateContexts[var3]).getState()) == null) {
                    throw new IllegalArgumentException("States cannot be null");
                }

                if (var5.getVariable() == null) {
                    throw new IllegalArgumentException("One or more states do not belong to variables.");
                }

                int var6;
                if ((var6 = this.a.indexOf(var5.getVariable(), var4.getTime())) < 0) {
                    throw new IllegalStateException(String.format("State [%s] belonging to variable [%s] at time [%s] not found in distribution", var5.getName(), var5.getVariable().getName(), var4.getTime()));
                }

                var2 += this.d[var6] * var5.getIndex();
            }

            return var2;
        }
    }

    public final double get(int index) {
        return this.c[index];
    }

    public final void set(int index, double value) {
        if (this.g != null || this.h) {
            this.b();
        }

        this.c[index] = value;
    }

    public final VariableContextCollection getSortedVariables() {
        return this.a;
    }

    private static int[] a(Table var0, Table var1, Integer var2, Integer var3) {
        VariableContextCollection var4 = var0.a;
        VariableContextCollection var5 = var1.a;
        int[] var6 = new int[var4.size()];

        for (int var7 = 0; var7 < var6.length; ++var7) {
            Integer var8;
            if ((var8 = var4.get(var7).getTime()) != null && var2 != null) {
                var8 = var8 + var2;
            }

            if (var8 != null && var3 != null) {
                var8 = var8 - var3;
            }

            int var9 = var5.indexOf(var4.get(var7).getVariable(), var8);
            var6[var7] = var9;
            if (var9 == -1) {
                throw new IllegalArgumentException("One table must contain the variables of the other table");
            }

            if (var7 > 0 && var9 <= var6[var7 - 1]) {
                throw new IllegalStateException("Table variables must be sorted");
            }
        }

        return var6;
    }

    public final double sum() {
        double var1 = 0.0;
        int var3 = this.c.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            var1 += this.c[var4];
        }

        return var1;
    }

    public final Table getTable() {
        return this;
    }

    public final Distribution divide(Distribution subset) {
        if (subset == null) {
            throw new NullPointerException("subset");
        } else if (!this.a.containsAll(subset.getSortedVariables(), true)) {
            throw new IllegalArgumentException("This instance does not contain all variable contexts found in subset.");
        } else {
            Table var2;
            (var2 = this.copy().getTable()).divideInPlace(subset.getTable());
            return var2;
        }
    }

    public final void divideInPlace(Table subset) {
        this.b();
        if (subset == null) {
            throw new NullPointerException("subset");
        } else {
            int var3;
            for (int var2 = 0; var2 < subset.a.size(); ++var2) {
                if ((var3 = this.a.indexOf(subset.a.get(var2), true)) < 0) {
                    throw new IllegalArgumentException("This instance does not contain all the variable contexts found in subset.");
                }

                if (subset.a.get(var2).isHead()) {
                    if (this.a.get(var3).isTail()) {
                        throw new IllegalArgumentException("Subset variable appears as a head variable in this instance.");
                    }

                    this.a.get(var3).a(HeadTail.TAIL);
                }
            }

            Table var10;
            double[] var11 = (var10 = new Table(subset.a)).c;
            double[] var4 = subset.c;

            for (int var5 = 0; var5 < var4.length; ++var5) {
                double var6;
                if ((var6 = var4[var5]) != 0.0) {
                    double var8;
                    if (Double.isInfinite(var8 = 1.0 / var6)) {
                        var11[var5] = 0.0;
                    } else {
                        var11[var5] = var8;
                    }
                }
            }

            this.multiplyInPlace(var10, false);

            for (var3 = 0; var3 < subset.a.size(); ++var3) {
                int var12 = this.a.indexOf(subset.a.get(var3), true);
                if (subset.a.get(var3).isHead()) {
                    if (this.a.get(var12).isTail()) {
                        throw new IllegalArgumentException("Subset variable appears as a head variable in this instance.");
                    }

                    this.a.get(var12).a(HeadTail.TAIL);
                }
            }

        }
    }

    public final Distribution instantiate(Double[] values) {
        if (values == null) {
            throw new NullPointerException("values");
        } else {
            Integer[] var2 = new Integer[values.length];

            for (int var3 = 0; var3 < values.length; ++var3) {
                if (values[var3] != null) {
                    var2[var3] = values[var3].intValue();
                }
            }

            return this.a(var2);
        }
    }

    public final Table instantiate(Integer[] values) {
        return this.a(values);
    }

    private Table a(Integer[] var1) {
        if (var1 == null) {
            throw new NullPointerException("values");
        } else {
            int var2;
            if ((var2 = var1.length) != this.a.size()) {
                throw new IllegalArgumentException("values count is invalid");
            } else if (var2 == 1 && var1[0] != null) {
                Table var8;
                (var8 = new Table(new Variable[0])).set(0, this.get(var1[0]));
                return var8;
            } else {
                bq var3 = new bq(2);
                bp var4 = new bp(2);
                bp var5 = new bp(2);

                for (int var6 = 0; var6 < var2; ++var6) {
                    if (var1[var6] != null) {
                        var4.a(var6);
                        var5.a(var1[var6]);
                    } else {
                        var3.a(this.a.get(var6));
                    }
                }

                if (var4.b == 0) {
                    throw new IllegalArgumentException("No instantiations were specified.");
                } else {
                    Table var9 = new Table(var3.a, var3.b);
                    (new bd(this.e, var1)).a(var9.c, this.c);
                    return var9;
                }
            }
        }
    }

    public final void marginalize(Distribution superset) {
        this.marginalize(superset, PropagationMethod.SUM);
    }

    public final void marginalize(Distribution superset, PropagationMethod propagation) {
        if (superset == null) {
            throw new NullPointerException("superset");
        } else {
            CLGaussian var3;
            if ((var3 = superset instanceof CLGaussian ? (CLGaussian) superset : null) == null) {
                this.a(superset.getTable(), true, (Integer) null, (Integer) null, propagation);
            } else {
                var3.marginalizeTo(this, propagation);
            }
        }
    }

    public final void marginalize(Table superset) {
        this.a(superset, true, (Integer) null, (Integer) null, PropagationMethod.SUM);
    }

    public final void marginalize(Table superset, PropagationMethod propagation) {
        this.a(superset, true, (Integer) null, (Integer) null, propagation);
    }

    public final void marginalize(Table superset, boolean initialize) {
        this.a(superset, initialize, (Integer) null, (Integer) null, PropagationMethod.SUM);
    }

    public final void marginalize(Table superset, boolean initialize, PropagationMethod propagation) {
        this.a(superset, initialize, (Integer) null, (Integer) null, propagation);
    }

    private void a(Table var1, boolean var2, Integer var3, Integer var4, PropagationMethod var5) {
        if (var1 == null) {
            throw new NullPointerException("superset");
        } else {
            this.b();
            int var6 = this.a.size();
            double[] var7 = var1.c;
            if (var6 == 0) {
                this.a(var1, var5);
            } else if (var6 == var1.a.size()) {
                this.a(var1, var6, var7);
            } else {
                int[] var8 = a(this, (Table) var1, (Integer) null, (Integer) null);

                for (int var9 = 0; var9 < var8.length; ++var9) {
                    this.a.get(var9).a(var1.a.get(var8[var9]).getHeadTail());
                }

                if (var6 == 1) {
                    this.a(var1, var2, var7, var8, var5);
                } else if (var8[var8.length - 1] == var8.length - 1) {
                    this.a(var1, var7, var8, var5);
                } else {
                    bb var11 = new bb(var8, var1.e.length);
                    a(var1.f, var8, var1.e, var11, var5).a(var7, this.c, var2, var5);
                }
            }
        }
    }

    private void a(Table var1, double[] var2, int[] var3, PropagationMethod var4) {
        int var5 = 1;

        int var6;
        for (var6 = var3.length; var6 < var1.e.length; ++var6) {
            var5 *= var1.e[var6];
        }

        var6 = var1.size() / var5;
        int var7 = 0;
        int var8;
        if (var5 == 1) {
            if (var4 == PropagationMethod.MAX) {
                for (var8 = 0; var8 < var6; ++var8) {
                    this.c[var8] = var2[var7++];
                }

            } else {
                for (var8 = 0; var8 < var6; ++var8) {
                    this.c[var8] = var2[var7++];
                }

            }
        } else if (var5 == 2) {
            if (var4 == PropagationMethod.MAX) {
                for (var8 = 0; var8 < var6; ++var8) {
                    this.c[var8] = Math.max(var2[var7++], var2[var7++]);
                }

            } else {
                for (var8 = 0; var8 < var6; ++var8) {
                    this.c[var8] = var2[var7++] + var2[var7++];
                }

            }
        } else {
            double var9;
            int var11;
            if (var4 == PropagationMethod.MAX) {
                for (var8 = 0; var8 < var6; ++var8) {
                    var9 = Double.NEGATIVE_INFINITY;

                    for (var11 = 0; var11 < var5; ++var11) {
                        var9 = Math.max(var9, var2[var7++]);
                    }

                    this.c[var8] = var9;
                }

            } else {
                for (var8 = 0; var8 < var6; ++var8) {
                    var9 = 0.0;

                    for (var11 = 0; var11 < var5; ++var11) {
                        var9 += var2[var7++];
                    }

                    this.c[var8] = var9;
                }

            }
        }
    }

    private void a(Table var1, boolean var2, double[] var3, int[] var4, PropagationMethod var5) {
        int var6 = var1.d[var4[0]];
        int var8 = this.e[0] - 1;
        int var9;
        int var10 = (var9 = var3.length) / var6;
        int var11 = 0;
        int var12 = 0;
        int var13;
        if (var2) {
            if (var5 == PropagationMethod.MAX) {
                for (var13 = 0; var13 < this.c.length; ++var13) {
                    this.c[var13] = Double.NEGATIVE_INFINITY;
                }
            } else {
                for (var13 = 0; var13 < this.c.length; ++var13) {
                    this.c[var13] = 0.0;
                }
            }
        }

        double[] var10000;
        double[] var18;
        if (var6 == 1) {
            if (var5 == PropagationMethod.MAX) {
                for (var13 = 0; var13 < var10; ++var13) {
                    var18 = this.c;
                    var18[var11] = Math.max(var18[var11], var3[var13]);
                    if (var11 == var8) {
                        var11 = 0;
                    } else {
                        ++var11;
                    }
                }

            } else {
                for (var13 = 0; var13 < var10; ++var13) {
                    var10000 = this.c;
                    var10000[var11] += var3[var13];
                    if (var11 == var8) {
                        var11 = 0;
                    } else {
                        ++var11;
                    }
                }

            }
        } else if (var5 == PropagationMethod.MAX) {
            for (var13 = 0; var13 < var10; ++var13) {
                for (int var17 = 0; var17 < var6; ++var17) {
                    var18 = this.c;
                    var18[var11] = Math.max(var18[var11], var3[var12++]);
                }

                if (var11 == var8) {
                    var11 = 0;
                } else {
                    ++var11;
                }
            }

        } else {
            for (var13 = 0; var13 < var10; ++var13) {
                double var14 = 0.0;

                for (int var16 = 0; var16 < var6; ++var16) {
                    var14 += var3[var12++];
                }

                var10000 = this.c;
                var10000[var11] += var14;
                if (var11 == var8) {
                    var11 = 0;
                } else {
                    ++var11;
                }
            }

        }
    }

    private void a(Table var1, int var2, double[] var3) {
        for (int var4 = 0; var4 < var2; ++var4) {
            this.a.get(var4).a(var1.a.get(var4).getHeadTail());
        }

        System.arraycopy(var3, 0, this.c, 0, this.c.length);
    }

    private void a(Table var1, PropagationMethod var2) {
        double var3;
        double[] var5;
        int var6;
        if (var2 == PropagationMethod.MAX) {
            var3 = Double.NEGATIVE_INFINITY;
            var5 = var1.c;

            for (var6 = 0; var6 < var5.length; ++var6) {
                var3 = Math.max(var3, var5[var6]);
            }

            this.c[0] = var3;
        } else {
            var3 = 0.0;
            var5 = var1.c;

            for (var6 = 0; var6 < var5.length; ++var6) {
                var3 += var5[var6];
            }

            this.c[0] = var3;
        }
    }

    public final void marginalizeLowMemory(Table[] tables) {
        this.marginalizeLowMemory(tables, new MarginalizeLowMemoryOptions());
    }

    public final void marginalizeLowMemory(Table[] tables, MarginalizeLowMemoryOptions options) {
        if (tables == null) {
            throw new NullPointerException("tables");
        } else {
            this.b();
            Cancellation var3;
            if ((var3 = options.getCancellation()) != null && var3.getCancel()) {
                throw new CancellationException();
            } else {
                List var4;
                int var5 = (var4 = a(tables, this)).size();
                int var6 = tables.length;

                int var7;
                for (var7 = 0; var7 < this.a.size(); ++var7) {
                    this.a.get(var7).a(HeadTail.TAIL);
                }

                for (var7 = 0; var7 < var6; ++var7) {
                    Table var8 = tables[var7];

                    for (int var9 = 0; var9 < this.a.size(); ++var9) {
                        if (!this.a.get(var9).isHead()) {
                            VariableContext var10 = this.a.get(var9);
                            int var11;
                            if ((var11 = var8.a.indexOf(var10, true)) >= 0 && var8.a.get(var11).isHead()) {
                                this.a.get(var9).a(HeadTail.HEAD);
                            }
                        }
                    }
                }

                this.setAll(0.0);
                int[] var21 = new int[var5];
                int var22 = var6 + 1;
                double[][] var23 = new double[var6][];

                for (int var24 = 0; var24 < var6; ++var24) {
                    var23[var24] = tables[var24].c;
                }

                eq[][] var25 = new eq[var5][];
                ArrayList var26 = new ArrayList(var22);

                int var17;
                int var18;
                int var20;
                for (int var12 = var5 - 1; var12 >= 0; --var12) {
                    var26.clear();
                    int var13 = ((VariableContext) var4.get(var12)).getVariable().getStates().size();
                    var21[var12] = var13 - 1;
                    VariableContext var14 = (VariableContext) var4.get(var12);

                    for (int var15 = 0; var15 < var22; ++var15) {
                        VariableContextCollection var16;
                        var17 = (var16 = var15 == var6 ? this.a : tables[var15].getSortedVariables()).size();
                        if ((var18 = var16.indexOf(var14, true)) >= 0) {
                            int var19 = 1;

                            for (var20 = var17 - 1; var20 > var18; --var20) {
                                var19 *= var16.get(var20).getVariable().getStates().size();
                            }

                            eq var33;
                            (var33 = new eq()).a = var15;
                            var33.b = var19;
                            var33.c = var19 * var21[var12];
                            var26.add(var33);
                        }
                    }

                    var25[var12] = (eq[]) var26.toArray(new eq[var26.size()]);
                }

                int[] var27 = new int[var5];
                int[] var28 = new int[var22];
                int var29 = 0;

                while (true) {
                    double var30 = 1.0;

                    for (var18 = 0; var18 < var6 && (var30 *= var23[var18][var28[var18]]) != 0.0; ++var18) {
                    }

                    if (var30 != 0.0) {
                        if (options.getPropagation() == PropagationMethod.SUM) {
                            double[] var10000 = this.c;
                            var10000[var28[var6]] += var30;
                        } else {
                            this.c[var28[var6]] = Math.max(this.c[var28[var6]], var30);
                        }
                    }

                    label109:
                    for (int var31 = var5 - 1; var31 >= 0; --var31) {
                        var17 = var27[var31];
                        var18 = var21[var31];
                        int var10001;
                        eq[] var32;
                        if (var17 != var18) {
                            int var10002 = var27[var31]++;
                            var32 = var25[var31];
                            var20 = 0;

                            while (true) {
                                if (var20 >= var32.length) {
                                    break label109;
                                }

                                var10001 = var32[var20].a;
                                var28[var10001] += var32[var20].b;
                                ++var20;
                            }
                        }

                        if (var31 == 0) {
                            return;
                        }

                        var27[var31] = 0;
                        var32 = var25[var31];

                        for (var20 = 0; var20 < var32.length; ++var20) {
                            var10001 = var32[var20].a;
                            var28[var10001] -= var32[var20].c;
                        }
                    }

                    if (var3 != null) {
                        ++var29;
                        if (var29 == 100000) {
                            var29 = 0;
                            if (var3 != null && var3.getCancel()) {
                                throw new CancellationException();
                            }
                        }
                    }
                }
            }
        }
    }

    private static List<VariableContext> a(Table[] var0, Table var1) {
        HashSet var2 = new HashSet();
        ArrayList var3 = new ArrayList();
        Network var4 = null;
        int var5 = 0;
        Table[] var6 = var0;
        int var7 = var0.length;

        int var8;
        int var11;
        for (var8 = 0; var8 < var7; ++var8) {
            VariableContextCollection var10;
            var11 = (var10 = var6[var8].getSortedVariables()).size();

            for (int var12 = 0; var12 < var11; ++var12) {
                VariableContext var13;
                Variable var14 = (var13 = var10.get(var12)).getVariable();
                Integer var15 = var13.getTime();
                if (var4 == null) {
                    if (var14.getNode() == null) {
                        throw new IllegalArgumentException("Table variable does not belong to a node");
                    }

                    if ((var4 = var14.getNode().getNetwork()) == null) {
                        throw new IllegalArgumentException("Table variable does not belong to a network");
                    }

                    var5 = var4.getVariables().size();
                } else {
                    if (var14.getNode() == null) {
                        throw new UnsupportedOperationException("Method does not support variables that do not belong to a network.");
                    }

                    if (var14.getNode().getNetwork() != var4) {
                        throw new IllegalStateException("Variables from different networks not supported");
                    }
                }

                int var16 = m.a(var14, var15, var5);
                if (var2.add(var16)) {
                    var3.add(var13);
                }
            }
        }

        VariableContextCollection var17;
        var7 = (var17 = var1.getSortedVariables()).size();

        for (var8 = 0; var8 < var7; ++var8) {
            VariableContext var9;
            Integer var18 = (var9 = var17.get(var8)).getTime();
            var11 = m.a(var9.getVariable(), var18, var5);
            if (!var2.contains(var11)) {
                throw new IllegalStateException("Tables must contain all the variables in this instance.");
            }
        }

        return var3;
    }

    public final Distribution multiply(Distribution distribution) {
        if (distribution == null) {
            throw new NullPointerException("distribution");
        } else {
            CLGaussian var2;
            if ((var2 = distribution instanceof CLGaussian ? (CLGaussian) distribution : null) != null) {
                return var2.multiply(this);
            } else {
                ArrayList var3 = new ArrayList();

                int var4;
                VariableContext var5;
                for (var4 = 0; var4 < this.a.size(); ++var4) {
                    var5 = this.a.get(var4);
                    var3.add(new VariableContext(var5.getVariable(), var5.getTime(), HeadTail.TAIL));
                }

                for (var4 = 0; var4 < distribution.getSortedVariables().size(); ++var4) {
                    var5 = distribution.getSortedVariables().get(var4);
                    if (!this.a.contains(var5, true)) {
                        var3.add(new VariableContext(var5.getVariable(), var5.getTime(), HeadTail.TAIL));
                    }
                }

                Table var6;
                (var6 = new Table(var3)).multiplyInPlace(this, true);
                var6.multiplyInPlace(distribution.getTable(), false);
                return var6;
            }
        }
    }

    public final void multiplyInPlace(double value) {
        this.b();
        if (value != 1.0) {
            int var3 = this.c.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                double[] var10000 = this.c;
                var10000[var4] *= value;
            }

        }
    }

    public final void multiplyInPlace(Table subset) {
        this.multiplyInPlace(subset, false);
    }

    public final void multiplyInPlace(Table subset, boolean initialize) {
        Object var8 = null;
        Object var7 = null;
        boolean var6 = false;
        Table var4 = subset;
        Table var3 = this;
        if (subset == null) {
            throw new NullPointerException("subset");
        } else {
            this.b();
            int var9 = this.a.size();
            int var10 = subset.a.size();
            int var11 = this.c.length;
            double[] var12;
            int var13 = (var12 = subset.c).length;
            double[] var10000;
            int var29;
            if (var10 == 0) {
                double var26 = var12[0];
                if (!initialize) {
                    for (var29 = 0; var29 < var11; ++var29) {
                        var10000 = var3.c;
                        var10000[var29] *= var26;
                    }

                    return;
                }

                for (var29 = 0; var29 < var11; ++var29) {
                    var3.c[var29] = var26;
                }
            } else {
                boolean var16;
                if (var10 == var9) {
                    int var25;
                    for (var25 = 0; var25 < var9; ++var25) {
                        boolean var28 = var4.a.get(var25).isHead();
                        var16 = var3.a.get(var25).isHead();
                        if (var28 && var16) {
                            throw new IllegalStateException(String.format("Variable [%s] is marked as head in both distributions.", var4.a.get(var25).getVariable().getName()));
                        }

                        if (var28) {
                            var3.a.get(var25).a(HeadTail.HEAD);
                        }
                    }

                    if (initialize) {
                        System.arraycopy(var12, 0, var3.c, 0, var12.length);
                        return;
                    }

                    for (var25 = 0; var25 < var13; ++var25) {
                        var10000 = var3.c;
                        var10000[var25] *= var12[var25];
                    }

                    return;
                }

                int[] var14 = a(subset, (Table) this, (Integer) null, (Integer) null);
                int var15 = 0;

                while (true) {
                    if (var15 >= var14.length) {
                        int var10001;
                        int var18;
                        int var24;
                        int var32;
                        if (var10 == 1) {
                            var15 = var3.d[var14[0]];
                            var29 = var4.e[0];
                            int var30 = var11 / (var15 * var29);
                            var18 = 0;
                            double var19;
                            int var20;
                            double var21;
                            int var23;
                            if (initialize) {
                                if (var15 > 1) {
                                    if (var15 == 2) {
                                        if (var29 == 2) {
                                            var19 = var12[0];
                                            var21 = var12[1];

                                            for (var23 = 0; var23 < var30; ++var23) {
                                                var3.c[var18++] = var19;
                                                var3.c[var18++] = var19;
                                                var3.c[var18++] = var21;
                                                var3.c[var18++] = var21;
                                            }

                                            return;
                                        } else {
                                            for (var32 = 0; var32 < var30; ++var32) {
                                                for (var20 = 0; var20 < var29; ++var20) {
                                                    var21 = var12[var20];
                                                    var3.c[var18++] = var21;
                                                    var3.c[var18++] = var21;
                                                }
                                            }

                                            return;
                                        }
                                    } else if (var29 == 2) {
                                        var19 = var12[0];
                                        var21 = var12[1];

                                        for (var23 = 0; var23 < var30; ++var23) {
                                            for (var24 = 0; var24 < var15; ++var24) {
                                                var3.c[var18++] = var19;
                                            }

                                            for (var24 = 0; var24 < var15; ++var24) {
                                                var3.c[var18++] = var21;
                                            }
                                        }

                                        return;
                                    } else {
                                        for (var32 = 0; var32 < var30; ++var32) {
                                            for (var20 = 0; var20 < var29; ++var20) {
                                                var21 = var12[var20];

                                                for (var23 = 0; var23 < var15; ++var23) {
                                                    var3.c[var18++] = var21;
                                                }
                                            }
                                        }

                                        return;
                                    }
                                } else if (var29 == 2) {
                                    var19 = var12[0];
                                    var21 = var12[1];

                                    for (var23 = 0; var23 < var30; ++var23) {
                                        var3.c[var18++] = var19;
                                        var3.c[var18++] = var21;
                                    }

                                    return;
                                } else {
                                    for (var32 = 0; var32 < var30; ++var32) {
                                        for (var20 = 0; var20 < var29; ++var20) {
                                            var3.c[var18++] = var12[var20];
                                        }
                                    }

                                    return;
                                }
                            } else if (var15 > 1) {
                                if (var15 == 2) {
                                    if (var29 == 2) {
                                        var19 = var12[0];
                                        var21 = var12[1];
                                        if (var30 == 1) {
                                            var10000 = var3.c;
                                            var10000[0] *= var19;
                                            var10000 = var3.c;
                                            var10000[1] *= var19;
                                            var10000 = var3.c;
                                            var10000[2] *= var21;
                                            var10000 = var3.c;
                                            var10000[3] *= var21;
                                            return;
                                        }

                                        for (var23 = 0; var23 < var30; ++var23) {
                                            var10000 = var3.c;
                                            var10001 = var18++;
                                            var10000[var10001] *= var19;
                                            var10000 = var3.c;
                                            var10001 = var18++;
                                            var10000[var10001] *= var19;
                                            var10000 = var3.c;
                                            var10001 = var18++;
                                            var10000[var10001] *= var21;
                                            var10000 = var3.c;
                                            var10001 = var18++;
                                            var10000[var10001] *= var21;
                                        }

                                        return;
                                    } else {
                                        for (var32 = 0; var32 < var30; ++var32) {
                                            for (var20 = 0; var20 < var29; ++var20) {
                                                var21 = var12[var20];
                                                var10000 = var3.c;
                                                var10001 = var18++;
                                                var10000[var10001] *= var21;
                                                var10000 = var3.c;
                                                var10001 = var18++;
                                                var10000[var10001] *= var21;
                                            }
                                        }

                                        return;
                                    }
                                } else if (var29 == 2) {
                                    var19 = var12[0];
                                    var21 = var12[1];

                                    for (var23 = 0; var23 < var30; ++var23) {
                                        for (var24 = 0; var24 < var15; ++var24) {
                                            var10000 = var3.c;
                                            var10001 = var18++;
                                            var10000[var10001] *= var19;
                                        }

                                        for (var24 = 0; var24 < var15; ++var24) {
                                            var10000 = var3.c;
                                            var10001 = var18++;
                                            var10000[var10001] *= var21;
                                        }
                                    }

                                    return;
                                } else {
                                    for (var32 = 0; var32 < var30; ++var32) {
                                        for (var20 = 0; var20 < var29; ++var20) {
                                            var21 = var12[var20];

                                            for (var23 = 0; var23 < var15; ++var23) {
                                                var10000 = var3.c;
                                                var10001 = var18++;
                                                var10000[var10001] *= var21;
                                            }
                                        }
                                    }

                                    return;
                                }
                            } else {
                                if (var29 != 2) {
                                    for (var32 = 0; var32 < var30; ++var32) {
                                        for (var20 = 0; var20 < var29; ++var20) {
                                            var10000 = var3.c;
                                            var10001 = var18++;
                                            var10000[var10001] *= var12[var20];
                                        }
                                    }

                                    return;
                                }

                                var19 = var12[0];
                                var21 = var12[1];

                                for (var23 = 0; var23 < var30; ++var23) {
                                    var10000 = var3.c;
                                    var10001 = var18++;
                                    var10000[var10001] *= var19;
                                    var10000 = var3.c;
                                    var10001 = var18++;
                                    var10000[var10001] *= var21;
                                }

                                return;
                            }
                        } else {
                            bb var27;
                            if ((var27 = new bb(var14, var3.e.length)).a() == 1) {
                                fl var31;
                                int var34;
                                if ((var31 = var27.a(0)).a() == 0) {
                                    var18 = 1;

                                    for (var32 = var31.b + 1; var32 < var3.e.length; ++var32) {
                                        var18 *= var3.e[var32];
                                    }

                                    var32 = 0;
                                    double var33;
                                    if (var18 == 2) {
                                        if (initialize) {
                                            for (var34 = 0; var34 < var13; ++var34) {
                                                var33 = var12[var34];
                                                var3.c[var32++] = var33;
                                                var3.c[var32++] = var33;
                                            }

                                            return;
                                        } else {
                                            for (var34 = 0; var34 < var13; ++var34) {
                                                var33 = var12[var34];
                                                var10000 = var3.c;
                                                var10001 = var32++;
                                                var10000[var10001] *= var33;
                                                var10000 = var3.c;
                                                var10001 = var32++;
                                                var10000[var10001] *= var33;
                                            }

                                            return;
                                        }
                                    } else {
                                        if (!initialize) {
                                            for (var34 = 0; var34 < var13; ++var34) {
                                                var33 = var12[var34];

                                                for (var24 = 0; var24 < var18; ++var24) {
                                                    var10000 = var3.c;
                                                    var10001 = var32++;
                                                    var10000[var10001] *= var33;
                                                }
                                            }

                                            return;
                                        }

                                        for (var34 = 0; var34 < var13; ++var34) {
                                            var33 = var12[var34];

                                            for (var24 = 0; var24 < var18; ++var24) {
                                                var3.c[var32++] = var33;
                                            }
                                        }

                                        return;
                                    }
                                }

                                if (var31.b == var3.e.length - 1) {
                                    var18 = 0;
                                    var32 = var11 / var13;
                                    int var22;
                                    if (!initialize) {
                                        for (var34 = 0; var34 < var32; ++var34) {
                                            for (var22 = 0; var22 < var13; ++var22) {
                                                var10000 = var3.c;
                                                var10001 = var18++;
                                                var10000[var10001] *= var12[var22];
                                            }
                                        }

                                        return;
                                    }

                                    for (var34 = 0; var34 < var32; ++var34) {
                                        for (var22 = 0; var22 < var13; ++var22) {
                                            var3.c[var18++] = var12[var22];
                                        }
                                    }

                                    return;
                                }
                            }

                            a(var3.f, var14, var3.e, var27, PropagationMethod.SUM).a(var3.c, var12, initialize);
                            break;
                        }
                    }

                    var16 = var4.a.get(var15).isHead();
                    boolean var17 = var3.a.get(var14[var15]).isHead();
                    if (var16 && var17) {
                        throw new IllegalStateException(String.format("Variable [%s] is marked as head in both distributions.", var4.a.get(var15).getVariable().getName()));
                    }

                    if (var16) {
                        var3.a.get(var14[var15]).a(HeadTail.HEAD);
                    }

                    ++var15;
                }
            }

        }
    }

    public static final class MarginalizeLowMemoryOptions {
        private Cancellation a;
        private PropagationMethod b;

        public MarginalizeLowMemoryOptions() {
            this.b = PropagationMethod.SUM;
        }

        public final boolean equals(Object obj) {
            if (!(obj instanceof MarginalizeLowMemoryOptions)) {
                return false;
            } else {
                MarginalizeLowMemoryOptions var2 = (MarginalizeLowMemoryOptions) obj;
                return this.b == var2.b;
            }
        }

        public final int hashCode() {
            return this.b.hashCode();
        }

        public final Cancellation getCancellation() {
            return this.a;
        }

        public final void setCancellation(Cancellation value) {
            this.a = value;
        }

        public final PropagationMethod getPropagation() {
            return this.b;
        }

        public final void setPropagation(PropagationMethod value) {
            this.b = value;
        }
    }

    public static final class MaxValue {
        private double a;
        private int b;

        MaxValue(double value, int index) {
            this.a = value;
            this.b = index;
        }

        public final int getIndex() {
            return this.b;
        }

        public final double getValue() {
            return this.a;
        }
    }

    public interface NonZeroValues {
        void value(int var1, double var2);
    }
}
